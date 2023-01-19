package mx.mauriciogs.ittalent.ui.authentication.signin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.authentication.SignInUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import mx.mauriciogs.ittalent.ui.authentication.SignInExceptionHandler

class SignInViewModel: ViewModel() {

    private val signInUseCase = SignInUseCase()
    private val getProfileUseCase = GetProfileUseCase()

    private val signInExceptionHandler = SignInExceptionHandler()

    private val _signInUiModelState = MutableLiveData<SignInUIModel>()

    val signInUiModelState: LiveData<SignInUIModel>
        get() = _signInUiModelState

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
        when(val result = getProfileUseCase.getProfile(userCredential)) {
            is AuthResult.Success -> { signInSuccess(result.data.user) }
            is AuthResult.Error -> { signInError(result) }
        }
    }

    private fun signInSuccess(user: UserProfile?) {

        if (user != null) {
            // Actualizar los datos locales del usuario po rsi hay cambios
            Log.d("LOGIN", "Obttenido: ${user.fullName}")
        }
    }


    private suspend fun signInError(result: AuthResult.Error) = withContext(Dispatchers.Main) {
        result.exception.printStackTrace()

        emitUiState(showProgress = false, exception = result.exception)
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, signInSuccess: Boolean? = null) {
        val signInUiModel = SignInUIModel(showProgress, exception, signInSuccess)
        _signInUiModelState.value = signInUiModel
    }
}