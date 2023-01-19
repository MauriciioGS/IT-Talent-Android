package mx.mauriciogs.ittalent.data.auth.model

import com.google.firebase.auth.FirebaseUser
import mx.mauriciogs.ittalent.core.extensions.no

data class SignInResponse (
    val isSuccessfull: Boolean = Boolean.no(),
    val user: FirebaseUser
)