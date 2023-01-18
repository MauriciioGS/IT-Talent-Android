package mx.mauriciogs.ittalent.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.ui.authentication.SignUpExceptionHandler
import mx.mauriciogs.ittalent.ui.authentication.signup.util.Experience
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.global.extensions.*

class SignUpViewModel: ViewModel() {

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

    fun setUser(user: Boolean) {
        userT = user
    }

    fun getUser() {
        _isTalent.value = userT
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

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false,
                            successExperience: Boolean = false, showSuccess: Boolean? = null) {
        val signUpUiModel = SignUpUIModel(showProgress, exception, enableNextStep = enableContinueButton, successExperience, showSuccess)
        _signUpUIModel.value = signUpUiModel
    }

    fun signUpProfile(name: String, country: String, city: String, age: Int, phoneNum: String, resume: String, photoUri: String) {
        userSignUpCredentials.fullName = name
        userSignUpCredentials.country = country
        userSignUpCredentials.city = city
        userSignUpCredentials.age = age
        userSignUpCredentials.phoneNumber = phoneNum
        userSignUpCredentials.resume = resume
        userSignUpCredentials.photoUri = photoUri

        Log.d("USERCRED", "$userSignUpCredentials")
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
