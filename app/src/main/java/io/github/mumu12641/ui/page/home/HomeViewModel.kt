package io.github.mumu12641.ui.page.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(
        value = UiState(
            false, listOf(
                Device("BLE-01"),
                Device("BLE-02")
            )
        )
    )
    val uiState: StateFlow<UiState> get() = _uiState

    fun flipExpanded() {
        _uiState.update {
            it.copy(isExpanded = !it.isExpanded)
        }
    }
}

data class UiState(var isExpanded: Boolean, var devices: List<Device>)
data class Device(val name: String)