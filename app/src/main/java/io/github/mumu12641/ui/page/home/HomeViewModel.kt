package io.github.mumu12641.ui.page.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhzc0x.bluetooth.client.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.App.Companion.context
import io.github.mumu12641.R
import io.github.mumu12641.service.BLEService
import io.github.mumu12641.service.BluetoothState
import io.github.mumu12641.service.DEFAULT_BLUETOOTH_STATE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    ViewModel() {

    private val bluetoothService = BLEService()
    private val _isExpanded = MutableStateFlow(false)
    private val _bluetoothState = bluetoothService.bluetoothState
    private val _logs = MutableStateFlow<List<LogInfo>>(emptyList())

    val uiState: StateFlow<UiState> =
        combine(
            _isExpanded, _bluetoothState, _logs
        ) { _, bluetoothState, logs ->
            UiState(bluetoothState, logs)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState(DEFAULT_BLUETOOTH_STATE, emptyList())
        )

    init {
        Timber.plant(object : Timber.Tree() {
            private val date = Date()

            @SuppressLint("SimpleDateFormat")
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                date.time = System.currentTimeMillis()
                viewModelScope.launch {
                    val log = LogInfo(
                        SimpleDateFormat("HH:mm:ss").format(date.time),
                        message,
                        priority
                    )
                    if (!_logs.value.contains(log)
                    ) {
                        _logs.value +=
                            LogInfo(
                                SimpleDateFormat("HH:mm:ss").format(date.time),
                                message,
                                priority
                            )
                    }
                }
            }
        })

    }

    fun startScan() {
        if (bluetoothService.checkBlueToothIsOpen()) {
            bluetoothService.startScan()
        } else {
            Toast.makeText(
                context, context.getString(R.string.bluetooth_closed), Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun stopScan() {
        bluetoothService.stopScan()
    }

    fun connect(device: Device) {
        bluetoothService.connect(device)
    }

    fun disconnect() {
        bluetoothService.disconnect()
    }

    fun receiveData() {
        bluetoothService.receiveData()
    }
}

data class UiState(
    var bluetoothState: BluetoothState,
    var logs: List<LogInfo>
)

data class LogInfo(val times: String, val msg: String, val priority: Int = Log.DEBUG)