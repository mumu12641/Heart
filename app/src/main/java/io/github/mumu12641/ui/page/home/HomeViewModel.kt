package io.github.mumu12641.ui.page.home

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.App.Companion.context
import io.github.mumu12641.R
import io.github.mumu12641.service.BluetoothService
import io.github.mumu12641.service.BluetoothState
import io.github.mumu12641.service.DEFAULT_BLUETOOTH_STATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    ViewModel() {

    //    private val _uiState = MutableStateFlow(
//        value = UiState(false)
//    )
//    val uiState: StateFlow<UiState> get() = _uiState
    private val bluetoothService = BluetoothService()
    private val _isExpanded = MutableStateFlow(false)
    private val _bluetoothState = bluetoothService.bluetoothState

    val uiState: StateFlow<UiState> =
        combine(_isExpanded, _bluetoothState) { isExpanded, bluetoothState ->
            UiState(isExpanded, bluetoothState)
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = UiState(false, DEFAULT_BLUETOOTH_STATE)
            )
//    val bluetoothState by lazy { bluetoothService.bluetoothState }

//    init {
//        viewModelScope.launch {
//            bluetoothService.bluetoothState.collect {
//                _uiState.value.bluetoothState = it
//            }
//        }
//    }

    fun flipExpanded() {
        _isExpanded.value = !_isExpanded.value
    }

    fun check() {
        if (bluetoothService.bluetoothIsOpen()) {
            viewModelScope.launch(
                Dispatchers.IO
            ) {
                bluetoothService.search()
            }
        } else {
            Toast.makeText(
                context, context.getString(R.string.bluetooth_closed), Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun foundNewDevice(name: String?, MAC: String?) {
        if (name != null && MAC != null) {
            bluetoothService.foundNewDevice(name, MAC)
        }
    }


}

data class UiState(
    var isExpanded: Boolean, var bluetoothState: BluetoothState
)