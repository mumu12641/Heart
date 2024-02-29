package io.github.mumu12641.ui.page.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.mumu12641.UserProfile
import io.github.mumu12641.data.proto.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val userProfileFlow = userProfileRepository.userProfileFlow
//    val uiState: StateFlow<UiState> = combine(userProfileRepository.userProfileFlow)
//    { userProfile ->
//        UiState(userProfile)
//    }
//
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(),
//            initialValue = UiState(EMPTY_USER_PROFILE)
//        )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun initUserProfile() {
        viewModelScope.launch {
            userProfileFlow.collect { userProfile ->
                _uiState.update {
                    protoToUiState(userProfile)
                }
            }
        }
    }

    fun saveUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
//            userProfileRepository.updateUserProfile(_uiState.value.userProfile)
            userProfileRepository.updateUserProfile(
                uiStateToProto()
            )
        }
    }

    fun setName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun setAge(age: String) {
        _uiState.update { it.copy(age = age) }
    }

    fun setBMI(bmi: String) {
        _uiState.update { it.copy(bmi = bmi) }
    }

    fun setSmokingAge(smokingAge: String) {
        _uiState.update { it.copy(smokingAge = smokingAge) }
    }

    fun setSmokingFreq(smokingFreq: String) {
        _uiState.update { it.copy(smokingFreq = smokingFreq) }
    }

    fun changeHyperlipidemia() {
//        _uiState.value.hyperlipidemia = !_uiState.value.hyperlipidemia
        _uiState.update {
            it.copy(hyperlipidemia = !it.hyperlipidemia)
        }
    }

    fun changeDiabetes() {
        _uiState.update {
            it.copy(diabetes = !it.diabetes)
        }
    }

    fun changeHypertension() {
        _uiState.update {
            it.copy(hypertension = !it.hypertension)
        }
    }

    fun changeAtrialFibrillationValvularHeartDisease() {
        _uiState.update {
            it.copy(atrialFibrillationValvularHeartDisease = !it.atrialFibrillationValvularHeartDisease)
        }
    }

    fun changeCoronaryHeartDiseaseAcuteCoronarySyndrome() {
        _uiState.update {
            it.copy(coronaryHeartDiseaseAcuteCoronarySyndrome = !it.coronaryHeartDiseaseAcuteCoronarySyndrome)
        }
    }

    fun changeCoronaryHeartDisease() {
        _uiState.update {
            it.copy(coronaryHeartDisease = !it.coronaryHeartDisease)
        }
    }

    fun changeAcuteMyocardialInfarction() {
        _uiState.update {
            it.copy(acuteMyocardialInfarction = !it.acuteMyocardialInfarction)
        }
    }

    fun changeCerebralPalsy() {
        _uiState.update {
            it.copy(cerebralPalsy = !it.cerebralPalsy)
        }
    }


    data class UiState(
        var name: String = "",
        var age: String = "",
        var bmi: String = "",
        var smokingAge: String = "",
        var smokingFreq: String = "",
        var hyperlipidemia: Boolean = false,
        var diabetes: Boolean = false,
        var hypertension: Boolean = false,
        var atrialFibrillationValvularHeartDisease: Boolean = false,
        var coronaryHeartDiseaseAcuteCoronarySyndrome: Boolean = false,
        var coronaryHeartDisease: Boolean = false,
        var acuteMyocardialInfarction: Boolean = false,
        var cerebralPalsy: Boolean = false,
    )

    private fun protoToUiState(userProfile: UserProfile): UiState {
        return UiState(
            userProfile.name,
            userProfile.age,
            userProfile.bmi,
            userProfile.smokingAge,
            userProfile.smokingFreq,
            userProfile.hyperlipidemia,
            userProfile.diabetes,
            userProfile.hypertension,
            userProfile.atrialFibrillationValvularHeartDisease,
            userProfile.coronaryHeartDiseaseAcuteCoronarySyndrome,
            userProfile.coronaryHeartDisease,
            userProfile.acuteMyocardialInfarction,
            userProfile.cerebralPalsy
        )
    }

    private fun uiStateToProto(): UserProfile {
        _uiState.value.apply {
            return UserProfile.getDefaultInstance().toBuilder().setName(name).setAge(age)
                .setBmi(bmi).setSmokingAge(smokingAge).setSmokingFreq(smokingFreq)
                .setHyperlipidemia(hyperlipidemia).setDiabetes(diabetes)
                .setHypertension(hypertension)
                .setAtrialFibrillationValvularHeartDisease(atrialFibrillationValvularHeartDisease)
                .setCoronaryHeartDiseaseAcuteCoronarySyndrome(
                    coronaryHeartDiseaseAcuteCoronarySyndrome
                ).setCoronaryHeartDisease(coronaryHeartDisease)
                .setAcuteMyocardialInfarction(acuteMyocardialInfarction)
                .setCerebralPalsy(cerebralPalsy).build()
        }
    }


}

val EMPTY_USER_PROFILE = UserProfile.getDefaultInstance()