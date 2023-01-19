package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.data.auth.SignUpRepository
import mx.mauriciogs.ittalent.data.auth.exceptions.AuthException
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.model.CreateAccountResult

class CreateAccountUseCase {

    private val authenticationRepository = SignUpRepository()

    suspend fun signInEmailPass(userSignUpCredentials: UserSignUpCredentials): CreateAccountResult<Boolean> {
        return when (val result = authenticationRepository.signUpEmailPass(userSignUpCredentials)) {
            is AuthResult.Error -> {
                CreateAccountResult.Error(AuthException.AlreadyRegistered)
            }
            is AuthResult.Success -> {
                if (authenticationRepository.createUserAccount(userSignUpCredentials))
                    CreateAccountResult.Success(result.data.isSuccessfull)
                else
                    CreateAccountResult.Error(AuthException.FailureToCreateAccount)
            }
        }
    }

}
