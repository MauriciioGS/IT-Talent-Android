package mx.mauriciogs.ittalent.ui.authentication.signin

import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

data class SignInUIModel (val showProgress: Boolean, val exception: Exception? = null, val singInSuccess: UserProfile? = null)