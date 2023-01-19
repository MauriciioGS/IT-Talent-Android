package mx.mauriciogs.ittalent.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.ENTERPRISE_R_UT
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.domain.authentication.CreateAccountUseCase
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.ui.authentication.SignUpExceptionHandler
import mx.mauriciogs.ittalent.ui.authentication.signup.util.Experience
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials

class SignUpViewModel: ViewModel() {

    private val createAccountUseCase = CreateAccountUseCase()

    private val userSignUpCredentials = UserSignUpCredentials.empty()
    private var userT = Boolean.yes()

    private val _isTalent = MutableLiveData<Boolean>()
    val isTalent : LiveData<Boolean>
        get() = _isTalent

    private val _signUpUIModel = MutableLiveData<SignUpUIModel>()
    val signUpUIModel : LiveData<SignUpUIModel>
        get() = _signUpUIModel

    private val _userSignUpCredentials = MutableLiveData<UserSignUpCredentials>()
    val userSignUp : LiveData<UserSignUpCredentials>
        get() = _userSignUpCredentials

    fun getUserSignUpCredentials() {
        _userSignUpCredentials.value = userSignUpCredentials
    }

    fun getUser() {
        _isTalent.value = userT
    }

    fun setUserType(isTalent: Boolean) {
        userSignUpCredentials.userType = if (isTalent) Int.TALENT_UT() else Int.ENTERPRISE_R_UT()
        userT = isTalent
        _isTalent.value = isTalent
    }

    fun setUserEmail(email: String) {
        userSignUpCredentials.email = email
        emitUiState(enableContinueButton = true)
    }

    fun saveNamePass(pass: String, passConfirm: String, fullName: String) {
        val userCredentials = Credentials(userSignUpCredentials.email, pass, passConfirm, fullName)
        val (areInvalidCredentials, exception) = SignUpExceptionHandler().areInvalidUserCredentials(userCredentials)
        if (areInvalidCredentials) return emitUiState(exception = exception)
        userSignUpCredentials.password = pass
        userSignUpCredentials.fullName = fullName
        emitUiState(enableContinueButton = true)
    }

    fun saveSkills(profRole: String, profLevel: String, skills: List<String>) {
        userSignUpCredentials.profRole = profRole
        userSignUpCredentials.xpLevel = profLevel
        userSignUpCredentials.skills = skills
        emitUiState(enableContinueButton = true)
    }

    fun setExperience(experience: Experience) {
        userSignUpCredentials.experiences.add(experience)
        Log.d("XPLIST", "${userSignUpCredentials.experiences}")
        emitUiState(successExperience = true)
    }

    fun stopExperienceSuccess(successExperience: Boolean = false){
        emitUiState(successExperience = successExperience)
    }

    fun stopButtonContinue(enableContinueButton: Boolean = false) {
        emitUiState(enableContinueButton = enableContinueButton)
    }

    fun signUpProfile(name: String, country: String, city: String, age: Int, phoneNum: String, resume: String, photoUri: String) {
        userSignUpCredentials.fullName = name
        userSignUpCredentials.country = country
        userSignUpCredentials.city = city
        userSignUpCredentials.age = age
        userSignUpCredentials.phoneNumber = phoneNum
        userSignUpCredentials.resume = resume
        userSignUpCredentials.photoUri = photoUri

        //Log.d("USERCRED", "$userSignUpCredentials")

        if(userT) signUpEmailPass()
        else emitUiState(enableContinueButton = true)
    }

    fun signUpRecruiter(enterprise: String, role: String) {
        userSignUpCredentials.enterprise = enterprise
        userSignUpCredentials.role = role

        signUpEmailPass()
    }

    private fun signUpEmailPass() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = createAccountUseCase.signInEmailPass(userSignUpCredentials)
            withContext(Dispatchers.Main) {
                if (result) Log.d("LOGIN","Usuario loggeado")
                else Log.d("LOGIN","Algo paso")
                emitUiState(showProgress = false)
            }
        }
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false,
                            successExperience: Boolean = false, showSuccess: Boolean? = null) {
        val signUpUiModel = SignUpUIModel(showProgress, exception, enableNextStep = enableContinueButton, successExperience, showSuccess)
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
