package mx.mauriciogs.ittalent.data.useraccount.remote

import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.data.useraccount.models.GetProfileFirebaseResponse
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
}
