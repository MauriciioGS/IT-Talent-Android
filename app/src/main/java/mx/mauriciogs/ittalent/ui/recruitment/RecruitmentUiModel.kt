package mx.mauriciogs.ittalent.ui.recruitment

import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.talent.Talent

data class RecruitmentUiModel (val showProgress: Boolean, val exception: Exception?,
                               val showSuccessStepOne: MutableList<Job>?,
                               val showSuccessStepTwo: MutableList<Job>?,
                               val showSuccessStepThree: MutableList<Job>?,
                               val showSuccessStepFour: MutableList<Job>?,
                               val showSuccessUpdate: Boolean?,
                               val showSuccessGetApplicants: MutableList<Talent>?)

data class RecruitmentFinishUiModel (val showFinishRecruitment: List<String>?, val job: Job?)