package mx.mauriciogs.ittalent.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.model.ProviderType
import mx.mauriciogs.ittalent.data.auth.model.SignUpResponse
import mx.mauriciogs.ittalent.data.auth.model.success
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.domain.authentication.UserSignUpCredentials

class SignUpRepository {

    suspend fun signUpEmailPass(userSignUpCredentials: UserSignUpCredentials): AuthResult<SignUpResponse> {

        return withContext(Dispatchers.IO) {
            try {
                val result = FirebaseClient.auth
                    .createUserWithEmailAndPassword(userSignUpCredentials.email, userSignUpCredentials.password)
                    .await()
                AuthResult.Success(SignUpResponse(Boolean.yes(), result.user!!))
            } catch (exception: Exception) {
                exception.printStackTrace()
                AuthResult.Error(exception)
            }
        }
    }

    suspend fun createUserAccount(userSignUpCredentials: UserSignUpCredentials) = kotlin.runCatching {
        val user = hashMapOf(
            "userType" to userSignUpCredentials.userType,
            "email" to userSignUpCredentials.email,
            "fullName" to userSignUpCredentials.fullName,
            "country" to userSignUpCredentials.country,
            "city" to userSignUpCredentials.city,
            "age" to userSignUpCredentials.age,
            "phoneNum" to userSignUpCredentials.phoneNumber,
            "resume" to userSignUpCredentials.resume,
            "profRole" to userSignUpCredentials.profRole,
            "photoUrl" to userSignUpCredentials.photoUri,
            "xpLevel" to userSignUpCredentials.xpLevel,
            "skills" to if (userSignUpCredentials.skills != null) userSignUpCredentials.skills else "",
            "experiences" to userSignUpCredentials.experiences,
            // Recruiter
            "enterprise" to userSignUpCredentials.enterprise,
            "role" to userSignUpCredentials.role,

            "store" to userSignUpCredentials.store
        )
        FirebaseClient.db
            .collection("users")
            .document(userSignUpCredentials.email)
            .set(user).await()
    }.isSuccess

}
