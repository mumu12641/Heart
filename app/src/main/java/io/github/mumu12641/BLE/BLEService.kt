package io.github.mumu12641.BLE

import com.zhzc0x.bluetooth.BluetoothClient
import com.zhzc0x.bluetooth.client.Characteristic
import com.zhzc0x.bluetooth.client.ClientState
import com.zhzc0x.bluetooth.client.ClientType
import com.zhzc0x.bluetooth.client.ConnectState
import com.zhzc0x.bluetooth.client.Device
import io.github.mumu12641.App.Companion.context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class BLEService {

    private val TAG = "BLEService"

    private val scope = CoroutineScope(Dispatchers.IO)

    private var bluetoothClient: BluetoothClient =
        BluetoothClient(context, ClientType.BLE, null)

    private val _bluetoothState = MutableStateFlow(
        value = DEFAULT_BLUETOOTH_STATE
    )

    val bluetoothState: Flow<BluetoothState> get() = _bluetoothState


    fun init() {
        bluetoothClient.release()
        bluetoothClient =
            BluetoothClient(context, ClientType.BLE, null)
    }

    fun checkBlueToothIsOpen(): Boolean {
        return bluetoothClient.checkState() != ClientState.DISABLE
    }

    fun startScan() {
        init()
        _bluetoothState.update {
            it.copy(devices = emptyList(), bleState = BLEState.Scanning)
        }
        bluetoothClient.startScan(5000, onEndScan = {
            _bluetoothState.update { it.copy(bleState = BLEState.Done) }
        }) {
//            Timber.tag(TAG).d("startScan: ${it.name}, ${it.address}, ${it.type}")
            if (!_bluetoothState.value.devices.contains(it)) {
                _bluetoothState.value.devices += it
            }
        }
    }

    fun stopScan() {
        bluetoothClient.stopScan()
        _bluetoothState.update { it.copy(bleState = BLEState.None) }
    }

    fun connect(device: Device) {
        bluetoothClient.disconnect()
        bluetoothClient.stopScan()
        bluetoothClient.connect(device, 512) {
            when (it) {
                ConnectState.CONNECTED -> {
                    Timber.tag(TAG).d("connect: CONNECTED")
                    Timber.tag(TAG).d("supportedServices: ")
                    bluetoothClient.supportedServices()?.let {
                        it.forEach { service ->
                            Timber.tag(TAG).d("%s", service)
                            service.characteristics?.forEach { characteristic ->
                                if (characteristic.properties.contains(Characteristic.Property.NOTIFY)
                                ) {
                                    Timber.tag(TAG).d("characteristic is %s", characteristic)
                                    bluetoothClient.assignService(service)
                                    _bluetoothState.update {
                                        it.copy(
                                            service = service,
                                            receiveCharacteristic = characteristic,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    _bluetoothState.update {
                        it.copy(
                            bleState = BLEState.Connected,
                            connectedDevice = device,
                            services = bluetoothClient.supportedServices()
                        )
                    }
                }

                ConnectState.CONNECTING -> {
                    Timber.tag(TAG).d("connect: CONNECTING")
                }

                ConnectState.CONNECT_TIMEOUT -> Timber.tag(TAG).d("connect: CONNECT_TIMEOUT")
                ConnectState.CONNECT_ERROR -> Timber.tag(TAG).d("connect: CONNECT_ERROR")
                ConnectState.DISCONNECTED -> Timber.tag(TAG).d("connect: DISCONNECTED")
                ConnectState.RECONNECT -> Timber.tag(TAG).d("connect: RECONNECT")
            }
        }
    }

    fun disconnect() {
        bluetoothClient.disconnect()
        _bluetoothState.update {
            it.copy(
                connectedDevice = null,
                services = null,
                service = null,
                receiveCharacteristic = null,
            )
        }
    }

    fun saveECG() {
        if (_bluetoothState.value.bleState != BLEState.Saving) {
            Timber.tag(TAG).d("Saving ECG...")
            _bluetoothState.value.bleState = BLEState.Saving
        }
    }

    fun setStateFetching() {
        _bluetoothState.value.bleState = BLEState.Fetching
    }

    fun stopFetch() {
        _bluetoothState.value.bleState = BLEState.Stop
    }


    fun receiveData() {
        var num = 0
        var buffer: MutableList<Int>
        var s = ""
        val start = System.currentTimeMillis()
        bluetoothClient.receiveData(_bluetoothState.value.receiveCharacteristic!!.uuid) { data ->
            if (_bluetoothState.value.bleState != BLEState.Stop) {
                _bluetoothState.value.bleState = BLEState.Fetching
                val voltageStr = data.toString(Charsets.US_ASCII)
                s = voltageStr + s
                num++
                if (num == 64) {
                    num = 0
                    buffer =
                        s.split(" ").filter { it.length == 4 }.map { it.toInt() }.toMutableList()
                    s = ""
                    scope.launch {
                        buffer.removeAt(buffer.size - 1)
                        _bluetoothState.value.ecgData.addAll(0, buffer)
                        buffer.clear()
                        val time = System.currentTimeMillis() - start
                        val cnt = _bluetoothState.value.ecgData.count { it != 0 }
                        val len  = _bluetoothState.value.ecgData.size
                        Timber.tag(TAG)
                            .d("Update Data $time, $cnt, $len")
                        delay(100)
                    }
                }
            }
        }
    }
}



