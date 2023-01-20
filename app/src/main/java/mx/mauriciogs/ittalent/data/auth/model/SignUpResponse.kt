package mx.mauriciogs.ittalent.data.auth.model

import mx.mauriciogs.ittalent.core.extensions.no

data class SignUpResponse (
    val isSuccessfull: Boolean = Boolean.no(),
    val userType: Int,
    val authProvider: ProviderType? = null
)