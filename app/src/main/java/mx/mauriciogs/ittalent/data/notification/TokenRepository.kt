package mx.mauriciogs.ittalent.data.notification

import javax.inject.Inject

class TokenRepository @Inject constructor(private val tokenRemoteDataSource: TokenRemoteDataSource) {

    suspend fun createToken() = tokenRemoteDataSource.createToken()

    //suspend fun getStoredToken() = tokenLocalDataSource.readTokenFCM()

    suspend fun saveToken(idUser: String, tokenString: String): Result<Boolean> = tokenRemoteDataSource.saveToken(idUser, tokenString)

    suspend fun getToken(idUser: String) = tokenRemoteDataSource.getToken(idUser)
}