package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.core.extensions.no
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.useraccount.local.UserRepositoryLocal
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.data.useraccount.remote.UserRepositoryRemote
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import mx.mauriciogs.ittalent.domain.useraccount.toUserEntity
import javax.inject.Inject

class GetProfileUseCase  @Inject constructor(private val userLocalRepository: UserRepositoryLocal) {
    private val userRepositoryRemote = UserRepositoryRemote()

    suspend fun getProfileFirebase(userCredentials: Credentials) = userRepositoryRemote.getProfileFirebase(userCredentials.email)

    suspend fun getProfileLocal() = userLocalRepository.getUserProfile().toUserProfile()

    suspend fun updateUserProfile(userProfile: UserProfile) = try {
        userLocalRepository.updateUserProfile(userProfile.toUserEntity())
        AuthResult.Success(Boolean.yes())
    } catch (exception: Exception) {
        AuthResult.Error(exception)
    }
}