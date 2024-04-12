package io.github.mumu12641.BLE

import io.github.mumu12641.BLE.util.client.Characteristic
import io.github.mumu12641.BLE.util.client.Device
import io.github.mumu12641.BLE.util.client.Service

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
    var ecgData: MutableList<Int> = MutableList(ECG_DATA_SIZE) { 0 }
)

const val ECG_DATA_SIZE = 500
val DEFAULT_BLUETOOTH_STATE = BluetoothState()
