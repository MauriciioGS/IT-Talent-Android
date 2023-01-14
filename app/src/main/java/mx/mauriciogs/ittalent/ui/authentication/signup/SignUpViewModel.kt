package mx.mauriciogs.ittalent.ui.authentication.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.global.extensions.ENTERPRISE_R_UT
import mx.mauriciogs.ittalent.ui.global.extensions.PROJECT_R_UT
import mx.mauriciogs.ittalent.ui.global.extensions.TALENT_UT

class SignUpViewModel: ViewModel() {

    private val userSignUpCredentials = UserSignUpCredentials.empty()

    private val _signUpUIModel = MutableLiveData<SignUpUIModel>()
    val signUpUIModel : LiveData<SignUpUIModel>
        get() = _signUpUIModel

    private val _userSignUpCredentials = MutableLiveData<UserSignUpCredentials>()
    val userSignUp : LiveData<UserSignUpCredentials>
        get() = _userSignUpCredentials

    fun getUserSignUpCredentials() {
        _userSignUpCredentials.value = userSignUpCredentials
    }

    fun setUserType(type: Int) {
        when (type) {
            Int.TALENT_UT() -> userSignUpCredentials.userType = Int.TALENT_UT()
            Int.ENTERPRISE_R_UT() -> userSignUpCredentials.userType = Int.ENTERPRISE_R_UT()
            Int.PROJECT_R_UT() -> userSignUpCredentials.userType = Int.PROJECT_R_UT()
        }
    }

    fun setUserEmail(email: String) {
        userSignUpCredentials.email = email
        emitUiState(enableContinueButton = true)
    }

    fun setUserPass(pass: String) { userSignUpCredentials.password = pass }

    fun stopButtonContinue(enableContinueButton: Boolean = false) {
        emitUiState(enableContinueButton = enableContinueButton)
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false, showSuccess: Boolean? = null) {
        val signUpUiModel = SignUpUIModel(showProgress, exception, enableNextStep = enableContinueButton, showSuccess)
        _signUpUIModel.value = signUpUiModel
    }

    class SignUpFragmentsVMFactory(): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(SignUpViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel() as T
            }
            throw  java.lang.IllegalArgumentException("Clase ViewModel desconocida")
        }
    }
}
