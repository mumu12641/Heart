package io.github.mumu12641.ui.page.home

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.mumu12641.BLE.util.client.Device
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.App.Companion.context
import io.github.mumu12641.BLE.BLEService
import io.github.mumu12641.BLE.BluetoothState
import io.github.mumu12641.BLE.DEFAULT_BLUETOOTH_STATE
import io.github.mumu12641.R
import io.github.mumu12641.data.local.DefaultECGModelRepository
import io.github.mumu12641.util.FileUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ecgModelRepository: DefaultECGModelRepository,
    private val bluetoothService: BLEService
) :
    ViewModel() {

    private val TAG = "HomeViewModel"

    //    private val _saving = MutableStateFlow(false)
    private val _bluetoothState = bluetoothService.bluetoothState
    private val _logs = MutableStateFlow<List<LogInfo>>(emptyList())

    val uiState: StateFlow<UiState> =
        combine(
            _bluetoothState, _logs
        ) { bluetoothState, logs ->
            UiState(bluetoothState, logs)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState(DEFAULT_BLUETOOTH_STATE, emptyList())
        )

    init {
        Timber.plant(object : Timber.Tree() {

            private val date = Date()

            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                date.time = System.currentTimeMillis()
                viewModelScope.launch {
                    val dateFormat = SimpleDateFormat.getTimeInstance(
                        SimpleDateFormat.DEFAULT,
                        Locale.getDefault()
                    )
                    val log = LogInfo(
                        dateFormat.format(date.time),
                        message,
                        priority
                    )
                    if (!_logs.value.contains(log)
                    ) {
                        _logs.value +=
                            LogInfo(
                                dateFormat.format(date.time),
                                message,
                                priority
                            )
                    }
                }
            }
        })

    }

//    fun changeExpanded() {
//        _isExpanded.value = !_isExpanded.value
//    }

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
//        bluetoothService.readData()
    }

    fun saveECG() {
        bluetoothService.saveECG()
    }

    fun saveBitmap(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            Timber.tag(TAG).e("Save Error!")
//            _saving.value = false
            bluetoothService.stopFetch()
        }) {
            bluetoothService.stopFetch()
            ecgModelRepository.addECG(
                FileUtil.saveECG(bitmap, uiState.value.bluetoothState.ecgData)
            )
            bluetoothService.setStateFetching()
        }
    }

    data class UiState(
//        var saving: Boolean,
        var bluetoothState: BluetoothState,
        var logs: List<LogInfo>
    )


}

data class LogInfo(val times: String, val msg: String, val priority: Int = Log.DEBUG)
