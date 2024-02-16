package io.github.mumu12641.BLE

import com.zhzc0x.bluetooth.client.Characteristic
import com.zhzc0x.bluetooth.client.Device
import com.zhzc0x.bluetooth.client.Service

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
    var receiveCharacteristic: Characteristic? = null,
    var fetching: Boolean = false,
    var ecgData: MutableList<Float> = MutableList(100) { 0f }
)

val DEFAULT_BLUETOOTH_STATE = BluetoothState()
