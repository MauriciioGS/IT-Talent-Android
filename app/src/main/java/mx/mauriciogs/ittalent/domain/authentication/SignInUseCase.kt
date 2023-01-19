package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.data.auth.SignInRepository

class SignInUseCase {

    private val signInRepository = SignInRepository()

    suspend fun signInEmailPass(userCredentials: Credentials) = signInRepository.signInEmailPass(userCredentials)
}