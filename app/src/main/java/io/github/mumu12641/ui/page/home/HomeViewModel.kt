package io.github.mumu12641.ui.page.home

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.App.Companion.context
import io.github.mumu12641.R
import io.github.mumu12641.service.BluetoothService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = UiState(false)
    )
    val uiState: StateFlow<UiState> get() = _uiState

    private val bluetoothService = BluetoothService()

    val bluetoothState by lazy { bluetoothService.bluetoothState }

//    init {
//        viewModelScope.launch {
//            bluetoothService.bluetoothState.collect {
//                _uiState.value.bluetoothState = it
//            }
//        }
//    }

    fun flipExpanded() {
        _uiState.update {
            it.copy(isExpanded = !it.isExpanded)
        }
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
    var isExpanded: Boolean
)