package mx.mauriciogs.ittalent.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import mx.mauriciogs.ittalent.domain.useraccount.profile.UpdateProfileRecruiterUseCase
import javax.inject.Inject

@HiltViewModel
class MyProfileRecViewModel@Inject constructor(private val getProfileUseCase: GetProfileUseCase): ViewModel() {

    private val updateProfileRecruiterUseCase = UpdateProfileRecruiterUseCase()

    lateinit var profile: UserProfile

    private val _myProfileModelState = MutableLiveData<MyProfileUiModel>()

    val myProfileModelState: LiveData<MyProfileUiModel>
        get() = _myProfileModelState

    fun getProfile() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val profLocal = getProfileUseCase.getProfileLocal().toUserProfile()
            val result = getProfileUseCase.getProfileFirebaseByEmail(profLocal.email!!)
            withContext(Dispatchers.Main){
                when(result){
                    is AuthResult.Success -> {
                        profile = result.data.user!!
                        Log.d("PROF", "Get primero $profile")
                        emitUiState(showProgress = false, setUI = profile)
                    }
                    is AuthResult.Error -> emitUiState(showProgress = false, exception = result.exception)
                }
            }

        }
    }

    fun updateProfile(nombre: String, pais: String, ciudad: String, edad: Int, numTel: String, resumen: String,
                      photoUri: String, rol: String
    ) {
        profile.fullName = nombre
        profile.country = pais
        profile.city = ciudad
        profile.age = edad
        profile.phoneNum = numTel
        profile.resume = resumen
        profile.photoUrl = photoUri
        profile.role = rol

        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = updateProfileRecruiterUseCase.updateProfileRecruiter(profile)
            withContext(Dispatchers.Main) {
                when(result) {
                    is AuthResult.Success -> {
                        emitUiState(showProgress = false, showSuccess = Boolean.yes())
                        getProfile()
                    }
                    is AuthResult.Error -> emitUiState(showProgress = false, exception = result.exception)
                }
            }
        }
    }

    fun deleteProfile() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = updateProfileRecruiterUseCase.deleteProfileRecruiter(profile)
            withContext(Dispatchers.Main){
                when(result) {
                    is AuthResult.Success -> deleteLocalData()
                    is AuthResult.Error -> emitUiState(showProgress = false, exception = result.exception)
                }
            }
        }
    }

    private fun deleteLocalData() {
        viewModelScope.launch {
            when(val result = getProfileUseCase.deleteUser()) {
                is AuthResult.Success -> emitUiState(showProgress = false, showSuccessDeletion = result.data)
                is AuthResult.Error -> emitUiState(showProgress = false, exception = result.exception)
            }
        }
    }

    private fun emitUiState(setUI: UserProfile? = null, showProgress: Boolean = false, exception: Exception? = null,
                            showSuccessDeletion: Boolean? = null, showSuccess: Boolean? = null) {
        val myProfileUiModel = MyProfileUiModel(setUI, showProgress, showSuccessDeletion, exception, showSuccess)
        _myProfileModelState.value = myProfileUiModel
    }
}
