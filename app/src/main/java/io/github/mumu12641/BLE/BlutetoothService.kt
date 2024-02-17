package io.github.mumu12641.BLE

import android.util.Log
import com.zhzc0x.bluetooth.BluetoothClient
import com.zhzc0x.bluetooth.client.Characteristic
import com.zhzc0x.bluetooth.client.ClientState
import com.zhzc0x.bluetooth.client.ClientType
import com.zhzc0x.bluetooth.client.ConnectState
import com.zhzc0x.bluetooth.client.Device
import io.github.mumu12641.App.Companion.context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class BLEService {

    private val TAG = "BLEService"

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
            it.copy(
                devices = emptyList(), scanState = ScanState.Scanning,
                fetching = false
            )
        }
        bluetoothClient.startScan(5000, onEndScan = {
            _bluetoothState.update { it.copy(scanState = ScanState.Done) }
        }) {
            Timber.tag(TAG).d("startScan: ${it.name}, ${it.address}, ${it.type}")
            if (!_bluetoothState.value.devices.contains(it)) {
                _bluetoothState.value.devices += it
            }
        }
    }

    fun stopScan() {
        bluetoothClient.stopScan()
        _bluetoothState.update { it.copy(scanState = ScanState.None) }
    }

    fun connect(device: Device) {
        bluetoothClient.disconnect()
        bluetoothClient.stopScan()
        bluetoothClient.connect(device, 80) {
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
                                    bluetoothClient.assignService(service)
                                    _bluetoothState.update {
                                        it.copy(
                                            service = service,
                                            receiveCharacteristic = characteristic,

                                            fetching = false
                                        )
                                    }
                                }
                            }
                        }
                    }
                    _bluetoothState.update {
                        it.copy(
                            scanState = ScanState.Connected,
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
                fetching = false
            )
        }
    }

    fun receiveData() {
        bluetoothClient.receiveData(_bluetoothState.value.receiveCharacteristic!!.uuid) { data ->
            _bluetoothState.value.fetching = true
//            _bluetoothState.value.ecgData.add(0,(Random.nextFloat() - 0.5f) * 100)
            val voltageStr = data.toString(Charsets.US_ASCII);
            val len = voltageStr.length;
            if (len < 6) {
                val voltage = voltageStr.substring(0, voltageStr.length - 1).toFloat();
                _bluetoothState.value.ecgData.add(0, voltage)
                Log.d(TAG, "receiveData: ${_bluetoothState.value.ecgData}")
                Timber.tag(TAG).d("Receive Data: ${data.toString(Charsets.US_ASCII)}")
            }
        }
    }
}



