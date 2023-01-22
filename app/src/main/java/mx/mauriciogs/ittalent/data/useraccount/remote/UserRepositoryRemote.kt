package mx.mauriciogs.ittalent.data.useraccount.remote

import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.data.useraccount.models.GetProfileFirebaseResponse
import mx.mauriciogs.ittalent.data.useraccount.models.UpdateProfileFirebaseResponse
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

class UserRepositoryRemote {
    suspend fun getProfileFirebase(email: String): AuthResult<GetProfileFirebaseResponse> = withContext(Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("users")
                .document(email)
                .get()
                .await()
            AuthResult.Success(GetProfileFirebaseResponse(Boolean.yes(), result.toObject<UserProfile>()))
        } catch (exception: Exception){
            AuthResult.Error(exception)
        }
    }

    suspend fun updateProfileRecruiterFirebase(userProfile: UserProfile): AuthResult<UpdateProfileFirebaseResponse> = withContext(Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("users")
                .document(userProfile.email!!)
                .update(mutableMapOf<String, Any>(
                    Pair("age", userProfile.age!!),
                    Pair("city", userProfile.city!!),
                    Pair("country", userProfile.country!!),
                    Pair("enterprise", userProfile.enterprise!!),
                    Pair("fullName", userProfile.fullName!!),
                    Pair("phoneNum", userProfile.phoneNum!!),
                    Pair("photoUrl", userProfile.photoUrl!!),
                    Pair("resume", userProfile.resume!!),
                    Pair("role", userProfile.role!!)
                )
                )
                .await()
            AuthResult.Success(UpdateProfileFirebaseResponse(Boolean.yes()))
        } catch (exception: Exception){
            AuthResult.Error(exception)
        }
    }
}
