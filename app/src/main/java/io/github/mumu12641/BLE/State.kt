package io.github.mumu12641.BLE

import com.zhzc0x.bluetooth.client.Characteristic
import com.zhzc0x.bluetooth.client.Device
import com.zhzc0x.bluetooth.client.Service

sealed class BLEState {

    data object None : BLEState()
    data object Scanning : BLEState()
    data object Done : BLEState()
    data object Connected : BLEState()
    data object Fetching : BLEState()
    data object Saving : BLEState()
    data object Stop : BLEState()
}

data class BluetoothState(
    var bleState: BLEState = BLEState.None,
    var devices: List<Device> = emptyList(),
    var connectedDevice: Device? = null,
    var services: List<Service>? = null,
    var service: Service? = null,
    var receiveCharacteristic: Characteristic? = null,
    var ecgData: MutableList<Float> = MutableList(100) { 0f }
)

val DEFAULT_BLUETOOTH_STATE = BluetoothState()
