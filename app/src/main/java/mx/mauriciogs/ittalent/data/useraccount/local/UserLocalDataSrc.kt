package mx.mauriciogs.ittalent.data.useraccount.local

import mx.mauriciogs.ittalent.data.useraccount.local.entities.UserProfileEntity
import javax.inject.Inject

class UserLocalDataSrc @Inject constructor(private val userDao: UserDao) {

    suspend fun insertUserProfile(userProfileEntity: UserProfileEntity)
            = userDao.insertUserProfile(userProfileEntity)
}