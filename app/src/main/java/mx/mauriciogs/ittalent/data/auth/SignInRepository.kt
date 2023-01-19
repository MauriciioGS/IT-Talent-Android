package mx.mauriciogs.ittalent.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.model.SignInResponse
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.domain.authentication.Credentials

class SignInRepository {
    suspend fun signInEmailPass(userCredential: Credentials): AuthResult<SignInResponse> = withContext(Dispatchers.IO) {
        try {
            val result = FirebaseClient.auth
                .signInWithEmailAndPassword(userCredential.email, userCredential.password)
                .await()
            AuthResult.Success(SignInResponse(Boolean.yes(), result.user!!))
        } catch (exception: Exception) {
            exception.printStackTrace()
            AuthResult.Error(exception)
        }
    }
}
