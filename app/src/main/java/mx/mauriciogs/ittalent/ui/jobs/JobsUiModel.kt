package mx.mauriciogs.ittalent.ui.jobs

import mx.mauriciogs.ittalent.domain.jobs.Job

//data class JobsUiModel (val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?, val showSuccess: Boolean?)
data class JobsUiModel (val setUI: String?, val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?, val showSuccess: Boolean?)
data class GetJobsUiModel (val setUI: String?, val showProgress: Boolean, val enableNextStep: Boolean, val exception: Exception?, val showSuccess: MutableList<Job>?)