package mx.mauriciogs.ittalent.data.useraccount.models

import mx.mauriciogs.ittalent.core.extensions.no
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

data class GetProfileFirebaseResponse(
    val isSuccessfull: Boolean = Boolean.no(),
    val user: UserProfile?
)

data class UpdateProfileFirebaseResponse(
    val isSuccessfull: Boolean = Boolean.no()
)