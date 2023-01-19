package mx.mauriciogs.ittalent.data.auth.exceptions

sealed class AuthException(override val message: String?) : Exception() {
    object AlreadyRegistered : AuthException("Parece ser que ya te has registrado")
    object FailureToCreateAccount : AuthException("Hubo un error al crear la cuenta, intenta de nuevo más tarde")
}
