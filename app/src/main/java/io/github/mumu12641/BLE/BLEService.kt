package io.github.mumu12641.BLE

import android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH
import io.github.mumu12641.BLE.util.BluetoothClient
import io.github.mumu12641.BLE.util.client.Characteristic
import io.github.mumu12641.BLE.util.client.ClientState
import io.github.mumu12641.BLE.util.client.ConnectState
import io.github.mumu12641.BLE.util.client.Device
import io.github.mumu12641.MainActivity.Companion.context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class BLEService {

    private val TAG = "BLEService"

    private val scope = CoroutineScope(Dispatchers.IO)

    private var bluetoothClient: BluetoothClient =
        BluetoothClient(context, null)

    private val _bluetoothState = MutableStateFlow(
        value = DEFAULT_BLUETOOTH_STATE
    )

    val bluetoothState: Flow<BluetoothState> get() = _bluetoothState


    fun init() {
        bluetoothClient.release()
        bluetoothClient =
            BluetoothClient(context, null)
    }

    fun checkBlueToothIsOpen(): Boolean {
        return bluetoothClient.checkState() != ClientState.DISABLE
    }

    fun startScan() {
        init()
        _bluetoothState.update {
            it.copy(devices = emptyList(), bleState = BLEState.Scanning)
        }
        bluetoothClient.startScan(2000, onEndScan = {
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
        bluetoothClient.connect(device, 203) {
            when (it) {
                ConnectState.CONNECTED -> {
                    bluetoothClient.requestConnectionPriority(CONNECTION_PRIORITY_HIGH)
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
        var s = ByteArray(0)
        var startTime: Long = 0
        var start = true
        _bluetoothState.value.ecgData = MutableList(ECG_DATA_SIZE) { 0 }
        Timber.tag(TAG).d("start Update data")
        Timber.tag(TAG).d(_bluetoothState.value.bleState.toString())
        bluetoothClient.receiveData(_bluetoothState.value.receiveCharacteristic!!.uuid) { data ->
            if (_bluetoothState.value.bleState != BLEState.Stop) {
                _bluetoothState.value.bleState = BLEState.Fetching
                if (start) {
                    startTime = System.currentTimeMillis()
                    start = false
                }
                s = data + s
                if (s.size >= 200 * 4) {
                    buffer = s.map { (it.toInt() and 0xFF) + 2394 }.toMutableList()
                    s = ByteArray(0)
                    scope.launch {
                        _bluetoothState.value.ecgData.addAll(0, buffer)
                        buffer.clear()
                        val time = (System.currentTimeMillis() - startTime) / 1000f
                        val cnt = _bluetoothState.value.ecgData.count { it != 0 }
                        val len = _bluetoothState.value.ecgData.size
                        Timber.tag(TAG)
                            .d("Update Data, $time, $cnt, $len")
                    }
                }
            }
        }
    }
}



