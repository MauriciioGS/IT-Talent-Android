package mx.mauriciogs.ittalent.data.useraccount.local

import mx.mauriciogs.ittalent.data.useraccount.local.entities.UserProfileEntity
import javax.inject.Inject

class UserRepositoryLocal @Inject constructor(private val userLocalDataSrc: UserLocalDataSrc) {

    suspend fun insertUserProfile(userProfileEntity: UserProfileEntity) =
            userLocalDataSrc.insertUserProfile(userProfileEntity)

    suspend fun updateUserProfile(userProfileEntity: UserProfileEntity) =
            userLocalDataSrc.updateUserProfile(userProfileEntity)

    suspend fun getUserProfile() = userLocalDataSrc.getUserProfile()

    suspend fun deleteUser() = userLocalDataSrc.deleteUser()

}