package mx.mauriciogs.ittalent.ui.authentication.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.authentication.SignInUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import mx.mauriciogs.ittalent.ui.authentication.SignInExceptionHandler
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) : ViewModel() {

    private val signInUseCase = SignInUseCase()

    private val signInExceptionHandler = SignInExceptionHandler()

    private val _signInUiModelState = MutableLiveData<SignInUIModel>()

    val signInUiModelState: LiveData<SignInUIModel>
        get() = _signInUiModelState

    fun getCredentials() {
        viewModelScope.launch {
            val profileLocal = getProfileUseCase.getProfileLocal()
            if (profileLocal != null) {
                val profile = profileLocal.toUserProfile()
                profile.email?.let { profile.password?.let { it1 -> Credentials(it, it1) } }?.let { signInEmailPass(it) }
            }
        }
    }

    fun signInEmailPass(userCredential: Credentials) {
        val (areInvalidCredentials, exception) = signInExceptionHandler.areInvalidUserCredentials(userCredential)
        if (areInvalidCredentials) return emitUiState(exception = exception)

        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = signInUseCase.signInEmailPass(userCredential)) {
                is AuthResult.Success -> getProfile(userCredential)
                is AuthResult.Error -> signInError(result)
            }
        }
    }

    private suspend fun getProfile(userCredential: Credentials) {
        Log.d("LOGIN", "Logueado, a obtener perfil C:")
        when(val result = getProfileUseCase.getProfileFirebase(userCredential)) {
            is AuthResult.Success -> { signInSuccess(result.data.user, userCredential.password) }
            is AuthResult.Error -> { signInError(result) }
        }
    }

    private suspend fun signInSuccess(user: UserProfile?, password: String) {
        if (user != null) {
            user.password = password
            when(val result = getProfileUseCase.updateUserProfile(user)) {
                is AuthResult.Success -> logInUser(user)
                is AuthResult.Error -> signInError(result)
            }
        }
    }

    private suspend fun logInUser(user: UserProfile) = withContext(Dispatchers.Main) {
        Log.d("LOGIN", "Actualizado local y obtuve: $user")
        emitUiState(showProgress = false, signInSuccess = user)
    }


    private suspend fun signInError(result: AuthResult.Error) = withContext(Dispatchers.Main) {
        result.exception.printStackTrace()

        emitUiState(showProgress = false, exception = result.exception)
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, signInSuccess: UserProfile? = null) {
        val signInUiModel = SignInUIModel(showProgress, exception, signInSuccess)
        _signInUiModelState.value = signInUiModel
    }

}
