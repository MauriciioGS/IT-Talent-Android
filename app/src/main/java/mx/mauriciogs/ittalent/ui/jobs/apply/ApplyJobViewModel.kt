package mx.mauriciogs.ittalent.ui.jobs.apply

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.no
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.jobs.exception.ApplyJobException
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.jobs.NewApplicantUseCase
import mx.mauriciogs.ittalent.ui.jobs.ApplyJobUiModel
import mx.mauriciogs.ittalent.ui.jobs.GetJobsTalentUiModel

class ApplyJobViewModel: ViewModel() {

    private val newApplicantUseCase = NewApplicantUseCase()

    private var _appliJobModelState = MutableLiveData<ApplyJobUiModel>()
    val appliJobModelState: LiveData<ApplyJobUiModel>
        get() = _appliJobModelState

    fun setNewApplicant(applicantEmail: String, job: Job) {
        emitUiApplyState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val newapplicants = listOf(applicantEmail)
            job.applicants = job.applicants!! + newapplicants
            val result = newApplicantUseCase.addNewApplicant(job.applicants!!, job.id!!)
            withContext(Dispatchers.Main){
                if (result) emitUiApplyState(showProgress = Boolean.no(), showSuccess = job.job)
                else emitUiApplyState(showProgress = true, exception = ApplyJobException.UnrecognizedError)
            }
        }
    }

    fun stopSuccess() {
        emitUiApplyState(showSuccess = null)
    }

    private fun emitUiApplyState(showProgress: Boolean = false, exception: Exception? = null, showSuccess: String? = null) {
        val applyJobUiModel = ApplyJobUiModel(showProgress, exception, showSuccess)
        _appliJobModelState.value = applyJobUiModel
    }


}