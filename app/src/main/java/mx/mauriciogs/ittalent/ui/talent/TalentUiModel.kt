package mx.mauriciogs.ittalent.ui.talent

import mx.mauriciogs.ittalent.domain.talent.Talent

data class TalentUiModel (val setUI: String?, val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?,
                           val showSuccess: Boolean?, val showSuccessFiler: MutableList<Talent>?)