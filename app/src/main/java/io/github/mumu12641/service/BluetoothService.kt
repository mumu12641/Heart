package io.github.mumu12641.service

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.util.Log
import io.github.mumu12641.App.Companion.context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class BluetoothService {

    private val TAG: String = "BluetoothService"

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

    val _bluetoothState = MutableStateFlow(
        value = BluetoothState(
            SearchState.None, emptyList()
        )

    )
    val bluetoothState: Flow<BluetoothState> get() = _bluetoothState


    // paired before
    val pairedDevices: Set<BluetoothDevice>? by lazy { bluetoothAdapter?.bondedDevices }
    fun bluetoothIsOpen(): Boolean {
        return bluetoothAdapter!!.isEnabled
    }

    fun search() {
        if (bluetoothAdapter?.isDiscovering == false) {
            _bluetoothState.update {
                it.copy(
                    searchState = SearchState.Searching,
                    devices = emptyList()
                )
            }
            bluetoothAdapter.startDiscovery()
        } else {
            Log.d(TAG, "search: isDiscovering == true")
        }
    }

    fun foundNewDevice(name: String, MAC: String) {
        _bluetoothState.value.devices += Device(name, MAC)
    }
}


sealed class SearchState {
    data object Searching : SearchState()
    data object None : SearchState()
}

data class Device(val name: String, val MAC: String)

data class BluetoothState(var searchState: SearchState, var devices: List<Device>)

val DEFAULT_BLUETOOTH_STATE = BluetoothState(SearchState.None, emptyList())