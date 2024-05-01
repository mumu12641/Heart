package io.github.mumu12641.ui.page.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.data.local.DefaultECGModelRepository
import io.github.mumu12641.data.local.model.ECGModel
import io.github.mumu12641.data.remote.NetworkRepository
import io.github.mumu12641.util.FileUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val ecgModelRepository: DefaultECGModelRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val TAG = "HistoryViewModel"
    private val _expandIndex = MutableStateFlow(-1)
    private val _loadState = MutableStateFlow<LoadState>(LoadState.None)
    val uiState: StateFlow<UiState> = combine(
        ecgModelRepository.ecgModels, _expandIndex, _loadState
    ) { ecgModels, expandIndex, loadstate ->
        UiState(ecgModels, expandIndex, loadstate)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UiState(emptyList(), -1, LoadState.None)
    )

    fun setExpandIndex(index: Int) {
        _expandIndex.value = index
    }

    fun deleteECG(ecgModel: ECGModel) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, _ ->
            Timber.tag(TAG).e("Delete error")
        }) {
            _expandIndex.value = -1
            ecgModelRepository.deleteECG(ecgModel)
            FileUtil.removeECGFile(ecgModel)
        }

    }

    fun updateECG(ecgModel: ECGModel) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, t ->
            Timber.tag(TAG).d(t)
//            ecgModelWithState.loadState = LoadState.None
            _loadState.value = LoadState.None
        }) {
//            _expandIndex.value = -1
//            ecgModelWithState.loadState = LoadState.Loading
            _loadState.value = LoadState.Loading
            val file = File(ecgModel.wavPath)
            val requestBody = file.asRequestBody("audio/wav".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = networkRepository.uploadWavFile(part)
            ecgModel.des = response.string()
            ecgModelRepository.updateECG(ecgModel)
            _loadState.value = LoadState.None
        }
    }

    data class UiState(
        val ecgModels: List<ECGModel>, val expandIndex: Int, val loadState: LoadState

    )

    sealed class LoadState {
        data object Loading : LoadState()
        data object None : LoadState()
    }

}