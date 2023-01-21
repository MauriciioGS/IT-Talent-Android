package mx.mauriciogs.ittalent.data.useraccount.local

import androidx.room.*
import mx.mauriciogs.ittalent.data.useraccount.local.entities.TABLE_USER_PROFILE
import mx.mauriciogs.ittalent.data.useraccount.local.entities.UserProfileEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfileEntity: UserProfileEntity)

    @Query(GET_USER_PROFILE)
    suspend fun getUserProfile(): UserProfileEntity

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserProfile(userProfileEntity: UserProfileEntity)

    @Query(DELETE_USER)
    suspend fun deleteUser(): Int

}

private const val GET_USER_PROFILE = "SELECT * FROM $TABLE_USER_PROFILE"
    private const val DELETE_USER = "DELETE FROM $TABLE_USER_PROFILE"
