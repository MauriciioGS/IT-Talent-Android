package mx.mauriciogs.ittalent.domain.authentication

import mx.mauriciogs.ittalent.ui.global.extensions.empty

data class Credentials(val email: String,
                       val password: String = String.empty(),
                       val passwordConfirm: String = String.empty(),
                       val name: String = String.empty())