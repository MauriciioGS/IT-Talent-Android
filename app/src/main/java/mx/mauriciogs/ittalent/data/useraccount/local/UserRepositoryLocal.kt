package mx.mauriciogs.ittalent.data.useraccount.local

import mx.mauriciogs.ittalent.data.useraccount.local.entities.UserProfileEntity
import javax.inject.Inject

class UserRepositoryLocal @Inject constructor(private val userLocalDataSrc: UserLocalDataSrc) {

    suspend fun insertUserProfile(userProfileEntity: UserProfileEntity) =
            userLocalDataSrc.insertUserProfile(userProfileEntity)

}