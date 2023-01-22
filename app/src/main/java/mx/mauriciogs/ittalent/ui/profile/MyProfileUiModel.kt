package mx.mauriciogs.ittalent.ui.profile

import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

data class MyProfileUiModel (val setUI: UserProfile?, val showProgress: Boolean, val showSuccessDeletion: Boolean?, val exception: Exception?,
                        val showSuccess: Boolean?)