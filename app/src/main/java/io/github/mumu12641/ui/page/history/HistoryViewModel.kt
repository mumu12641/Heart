package io.github.mumu12641.ui.page.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.data.local.DefaultECGModelRepository
import io.github.mumu12641.data.local.model.ECGModel
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
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val ecgModelRepository: DefaultECGModelRepository,
) : ViewModel() {

    private val TAG = "HistoryViewModel"
    private val _expandIndex = MutableStateFlow<Int>(-1)
    val uiState: StateFlow<UiState> = combine(
        ecgModelRepository.ecgModels, _expandIndex,
    ) { ecgModels, expandIndex ->
        UiState(ecgModels, expandIndex)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UiState(emptyList(), -1)
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

    data class UiState(
        val ecgModels: List<ECGModel>, val expandIndex: Int
    )
}