package io.github.mumu12641.service

import android.util.Log
import com.zhzc0x.bluetooth.BluetoothClient
import com.zhzc0x.bluetooth.client.Characteristic
import com.zhzc0x.bluetooth.client.ClientState
import com.zhzc0x.bluetooth.client.ClientType
import com.zhzc0x.bluetooth.client.ConnectState
import com.zhzc0x.bluetooth.client.Device
import com.zhzc0x.bluetooth.client.Service
import io.github.mumu12641.App.Companion.context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.nio.ByteBuffer

class BLEService {

    private val TAG = "BLEService"

    private var bluetoothClient: BluetoothClient =
        BluetoothClient(context, ClientType.BLE, null)

    private val _bluetoothState = MutableStateFlow(
        value = DEFAULT_BLUETOOTH_STATE
    )
    val bluetoothState: Flow<BluetoothState> get() = _bluetoothState


    fun checkBlueToothIsOpen(): Boolean {
        return bluetoothClient.checkState() != ClientState.DISABLE
    }

    fun startScan() {
        bluetoothClient.release()
        bluetoothClient =
            BluetoothClient(context, ClientType.BLE, null)
        _bluetoothState.update { it.copy(devices = emptyList(), scanState = ScanState.Scanning) }
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
                            service.characteristics?.forEach() { characteristic ->
                                if (characteristic.properties.contains(Characteristic.Property.NOTIFY)
                                ) {
                                    bluetoothClient.assignService(service)
                                    _bluetoothState.update {
                                        it.copy(
                                            service = service,
                                            receiveCharacteristic = characteristic
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
    }

    fun receiveData() {
        bluetoothClient.receiveData(_bluetoothState.value.receiveCharacteristic!!.uuid) { data ->

            Timber.tag(TAG).d("receiveData: ${data.toString(Charsets.US_ASCII)}")
        }
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHex(): String =
    asUByteArray().joinToString("") { it.toString(radix = 16).padStart(2, '0') }

sealed class ScanState {
    data object Scanning : ScanState()
    data object Done : ScanState()
    data object None : ScanState()
    data object Connected : ScanState()
}

data class BluetoothState(
    var scanState: ScanState = ScanState.None,
    var devices: List<Device> = emptyList(),
    var connectedDevice: Device? = null,
    var services: List<Service>? = null,
    var service: Service? = null,
    var receiveCharacteristic: Characteristic? = null
)

val DEFAULT_BLUETOOTH_STATE = BluetoothState()
