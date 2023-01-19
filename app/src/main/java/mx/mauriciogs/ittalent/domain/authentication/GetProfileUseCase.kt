package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.data.useraccount.remote.UserRepositoryRemote

class GetProfileUseCase {
    private val userRepositoryRemote = UserRepositoryRemote()

    suspend fun getProfile(userCredentials: Credentials) = userRepositoryRemote.getProfileFirebase(userCredentials.email)
}