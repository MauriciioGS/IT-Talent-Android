package mx.mauriciogs.ittalent.ui.authentication

import androidx.annotation.StringRes
import com.example.ittalent.R
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.ui.global.extensions.isValidEmail

class SignInExceptionHandler {

    fun invalidEmail(email: String) = when {
        email.isBlank() || !email.isValidEmail() -> Pair(true, SignInException.Email())
        else -> Pair(false, NoValidationNeeded)
    }

    fun areInvalidUserCredentials(userCredentials: Credentials) = when {

        userCredentials.email.isBlank() || !userCredentials.email.isValidEmail() -> Pair(true, SignInException.Email())

        userCredentials.password.isBlank() || userCredentials.password.length < MIN_CHARACTERS_PASSWORD -> Pair(true, SignInException.Password())

        else -> Pair(false, NoValidationNeeded)
    }

    companion object {
        const val MIN_CHARACTERS_PASSWORD = 5
    }
}

sealed class SignInException : Exception() {
    class Email(@StringRes val error: Int = R.string.error_invalid_email_form) : SignInException()
    class Password(@StringRes val error: Int = R.string.error_invalid_password) : SignInException()
}

object NoValidationNeeded : SignInException()