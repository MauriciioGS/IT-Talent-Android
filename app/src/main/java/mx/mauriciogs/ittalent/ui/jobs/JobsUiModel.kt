package mx.mauriciogs.ittalent.ui.jobs

data class JobsUiModel (val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?, val showSuccess: Boolean?)
data class NewJobUiModel (val setUI: String?, val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?, val showSuccess: Boolean?)