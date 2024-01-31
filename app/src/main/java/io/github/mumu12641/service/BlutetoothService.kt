package io.github.mumu12641.service

import android.util.Log
import android.widget.Toast
import com.zhzc0x.bluetooth.BluetoothClient
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
                    Log.d(TAG, "connect: CONNECTED")
                    if (bluetoothClient.supportedServices() == null) {
                        Log.d(TAG, "connect: null")
                    } else {
                        bluetoothClient.supportedServices()!!.let {
                            Log.d(TAG, "connect: ${it.size}")
                            it.forEach {
                                Log.d(TAG, "connect: ${it}")
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
                    Toast.makeText(context, "connecting", Toast.LENGTH_SHORT).show()
                }

                ConnectState.CONNECT_TIMEOUT -> Log.d(TAG, "connect: CONNECT_TIMEOUT")
                ConnectState.CONNECT_ERROR -> Log.d(TAG, "connect: CONNECT_ERROR")
                ConnectState.DISCONNECTED -> Log.d(TAG, "connect: DISCONNECTED")
                ConnectState.RECONNECT -> Log.d(TAG, "connect: RECONNECT")
//                else -> {
//                    Log.d(TAG, "connect: error")
//                    _bluetoothState.update {
//                        it.copy(
////                            scanState = ScanState.,
//                            connectedDevice = null
//                        )
//                    }
//                }
            }
        }
    }

    fun disconnect() {
        bluetoothClient.disconnect()
    }
}

sealed class ScanState {
    data object Scanning : ScanState()
    data object Done : ScanState()
    data object None : ScanState()
    data object Connected : ScanState()
}

data class BluetoothState(
    var scanState: ScanState,
    var devices: List<Device>,
    var connectedDevice: Device?,
    var services: List<Service>?
)

val DEFAULT_BLUETOOTH_STATE = BluetoothState(ScanState.None, emptyList(), null, null)