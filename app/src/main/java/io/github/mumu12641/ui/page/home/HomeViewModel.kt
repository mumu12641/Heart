package io.github.mumu12641.ui.page.home

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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
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

    //    private val _uiState = MutableStateFlow(
//        value = UiState(false)
//    )
//    val uiState: StateFlow<UiState> get() = _uiState
    private val bluetoothService = BLEService()
    private val _isExpanded = MutableStateFlow(false)
    private val _bluetoothState = bluetoothService.bluetoothState

    val uiState: StateFlow<UiState> =
        combine(_isExpanded, _bluetoothState) { _, bluetoothState ->
            UiState(bluetoothState, null)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState(DEFAULT_BLUETOOTH_STATE, null)
        )
//    val bluetoothState by lazy { bluetoothService.bluetoothState }

    init {
        val logFlow = channelFlow {
            Timber.plant(object : Timber.Tree() {
                private val date = Date()
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    date.time = System.currentTimeMillis()
                    trySend(
                        LogInfo(
                            SimpleDateFormat("HH:mm:ss").format(date.time),
                            message,
                            priority
                        )
                    )
                }
            })
            awaitClose {
                Timber.d("logChannelFLow closed")
            }

        }
        viewModelScope.launch {
            logFlow.collect {
                uiState.value.log = "[${it.times}], ${it.msg}"
            }
        }
//        lifecycleScope.launch {
//            logFlow.collect {
//                println("collect=$it")
//                while (logoutList.size >= 1000) {
//                    logoutList.removeAt(0)
//                }
//                logoutList.add(it)
//                scrollToBottom = !scrollToBottom
//            }
//        }
    }

//    fun flipExpanded() {
//        _isExpanded.value = !_isExpanded.value
//    }

    fun startScan() {
        if (bluetoothService.checkBlueToothIsOpen()) {
//            viewModelScope.launch(
//                Dispatchers.IO
//            ) {
            bluetoothService.startScan()
//            }
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

//    fun foundNewDevice(name: String?, MAC: String?) {
//        if (name != null && MAC != null) {
//            bluetoothService.foundNewDevice(name, MAC)
//        }
//    }


}

data class UiState(
    var bluetoothState: BluetoothState,
    var log: String?
)

data class LogInfo(val times: String, val msg: String, val priority: Int = Log.DEBUG)