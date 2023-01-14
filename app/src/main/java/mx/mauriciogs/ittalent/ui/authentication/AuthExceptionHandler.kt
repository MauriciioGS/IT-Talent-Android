package mx.mauriciogs.ittalent.ui.authentication

import androidx.annotation.StringRes
import com.example.ittalent.R
import mx.mauriciogs.ittalent.domain.authentication.Credentials
import mx.mauriciogs.ittalent.ui.global.extensions.isValidEmail

class SignInExceptionHandler {

    fun areInvalidUserCredentials(userCredentials: Credentials) = when {

        userCredentials.email.isBlank() || !userCredentials.email.isValidEmail() -> Pair(true, SignInException.Email())

        userCredentials.password.isBlank() || userCredentials.password.length < MIN_CHARACTERS_PASSWORD -> Pair(true, SignInException.Password())

        else -> Pair(false, NoValidationNeeded)
    }

    companion object {
        const val MIN_CHARACTERS_PASSWORD = 6
    }
}


class SignUpExceptionHandler {

    fun invalidEmail(email: String) = when {
        email.isBlank() || !email.isValidEmail() -> Pair(true, SignInException.Email())
        else -> Pair(false, NoValidationNeeded)
    }

    fun areInvalidUserCredentials(userCredentials: Credentials) = when {
        userCredentials.name.isBlank() || userCredentials.name.length <  CONSIDERABLE_NAME_SIZE -> Pair(true, SignUpExeption.Name())
        userCredentials.password.isBlank() || userCredentials.password.length < SignInExceptionHandler.MIN_CHARACTERS_PASSWORD -> Pair(true, SignInException.Password())
        userCredentials.password != userCredentials.passwordConfirm -> Pair(true, SignUpExeption.Passwords())
        else -> Pair(false, NoValidationNeeded)
    }

    companion object {
        const val CONSIDERABLE_NAME_SIZE = 10
    }

}

sealed class SignInException : Exception() {
    class Email(@StringRes val error: Int = R.string.error_invalid_email_form) : SignInException()
    class Password(@StringRes val error: Int = R.string.error_invalid_password) : SignInException()
}

sealed class SignUpExeption : Exception() {
    class Passwords(@StringRes val error: Int = R.string.error_invalid_passwords) : SignInException()
    class Name(@StringRes val error: Int = R.string.error_invalid_name) : SignInException()
}

object NoValidationNeeded : SignInException()