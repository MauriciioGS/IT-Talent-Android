package mx.mauriciogs.ittalent.data.auth.model

sealed class AuthResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
}
fun <T : Any> AuthResult<T>.error(message: String? = null) = (this as? AuthResult.Error)?.exception ?: Exception(message ?: "unknown error")

fun <T : Any> AuthResult<T>.error(exception: Exception) = (this as? AuthResult.Error)?.exception ?: exception

fun <T : Any> AuthResult<T>.success() = (this as? AuthResult.Success)?.data