package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.data.auth.SignUpRepository
import mx.mauriciogs.ittalent.data.auth.exceptions.AuthException
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.model.CreateAccountResult
import mx.mauriciogs.ittalent.data.useraccount.local.UserRepositoryLocal
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val userLocalRepository: UserRepositoryLocal) {

    private val authenticationRepository = SignUpRepository()

    suspend fun signInEmailPass(userSignUpCredentials: UserSignUpCredentials): CreateAccountResult<Int> {
        return when (val result = authenticationRepository.signUpEmailPass(userSignUpCredentials)) {
            is AuthResult.Error -> {
                CreateAccountResult.Error(AuthException.AlreadyRegistered)
            }
            is AuthResult.Success -> {
                val isAuth = authenticationRepository.createUserAccount(userSignUpCredentials)
                if (isAuth){
                    userLocalRepository.insertUserProfile(userSignUpCredentials.toUserEntity())
                    CreateAccountResult.Success(result.data.userType)
                }
                else CreateAccountResult.Error(AuthException.FailureToCreateAccount)
            }
        }
    }

}
