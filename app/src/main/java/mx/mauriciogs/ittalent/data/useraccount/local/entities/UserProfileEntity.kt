package mx.mauriciogs.ittalent.data.useraccount.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.mauriciogs.ittalent.core.extensions.default
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.data.useraccount.models.ExperienceItem
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

const val TABLE_USER_PROFILE = "user_profile"
@Entity(tableName = TABLE_USER_PROFILE)
data class UserProfileEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "userId") var id: Int = Int.default(),
    var userType: Int,
    var email: String,
    var password: String,
    var fullName: String,
    var country: String,
    var city: String,
    var age: Int,
    var phoneNumber: String,
    var resume: String,
    var profRole: String,
    var photoUri: String,
    var xpLevel: String,
    // Recruit
    var enterprise: String,
    var role: String,
)

fun UserProfileEntity.toUserProfile() = UserProfile(
    age = age,
    country = country,
    city = city,
    enterprise = enterprise,
    email = email,
    fullName = fullName,
    password = password,
    profRole = profRole,
    phoneNum = phoneNumber,
    photoUrl = photoUri,
    role = role,
    resume = resume,
    userType = userType,
    xpLevel = xpLevel,
)