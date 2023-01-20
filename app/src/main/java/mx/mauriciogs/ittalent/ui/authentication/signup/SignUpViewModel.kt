package mx.mauriciogs.ittalent.ui.authentication.signup

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.ENTERPRISE_R_UT
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.CreateAccountResult
import mx.mauriciogs.ittalent.domain.authentication.CreateAccountUseCase
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.domain.authentication.Experience
import mx.mauriciogs.ittalent.domain.authentication.UserSignUpCredentials
import mx.mauriciogs.ittalent.ui.authentication.SignUpExceptionHandler
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val createAccountUseCase: CreateAccountUseCase) : ViewModel() {

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

        userSignUpCredentials.skills = listOf("")

        signUpEmailPass()
    }

    private fun signUpEmailPass() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = createAccountUseCase.signInEmailPass(userSignUpCredentials)
            withContext(Dispatchers.Main) {
                when (result) {
                    is CreateAccountResult.Success ->{
                        signInSuccess(result.data)
                    }
                    is CreateAccountResult.Error -> {
                        signInError(result.exception)
                    }
                }
            }
        }
    }

    private fun signInSuccess(result: Int) {
        emitUiState(showProgress = false, showSuccess=result)
    }

    private fun signInError(exception: Exception) {
        exception.printStackTrace()
        emitUiState(showProgress = false, exception = exception)
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false,
                            successExperience: Boolean = false, showSuccess: Int? = null) {
        val signUpUiModel = SignUpUIModel(showProgress, exception, enableNextStep = enableContinueButton, successExperience, showSuccess)
        _signUpUIModel.value = signUpUiModel
    }

}
