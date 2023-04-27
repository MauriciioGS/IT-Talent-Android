package mx.mauriciogs.ittalent.domain.notification

import mx.mauriciogs.ittalent.data.notification.TokenRepository
import javax.inject.Inject

class TokenUseCase @Inject constructor(private val tokenRepository: TokenRepository) {

    suspend fun createToken() = tokenRepository.createToken()

    //suspend fun getStoredToken() = tokenRepository.getStoredToken()

    suspend fun saveToken(idUser: String, tokenString: String) = tokenRepository.saveToken(idUser, tokenString)

    suspend fun getToken(idUser: String) = tokenRepository.getToken(idUser)
}