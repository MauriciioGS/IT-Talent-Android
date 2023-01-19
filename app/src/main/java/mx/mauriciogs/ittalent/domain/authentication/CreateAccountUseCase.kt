package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.data.auth.SignUpRepository
import mx.mauriciogs.ittalent.data.auth.model.success
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials

class CreateAccountUseCase {

    private val authenticationRepository = SignUpRepository()

    suspend fun signInEmailPass(userSignUpCredentials: UserSignUpCredentials): Boolean {
        val accountCreated = authenticationRepository.signUpEmailPass(userSignUpCredentials)
        return if (accountCreated.success()?.isSuccessfull == true) //userAccountRepository.createUserTable(userSignUpCredentials)
                    true
                else false

    }
}