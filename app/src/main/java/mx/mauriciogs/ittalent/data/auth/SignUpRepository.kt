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
import mx.mauriciogs.ittalent.ui.authentication.signup.util.UserSignUpCredentials

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

}
