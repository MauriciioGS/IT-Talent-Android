package mx.mauriciogs.ittalent.ui.jobs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.jobs.PostJobUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) : ViewModel() {

    private val postJobUseCase = PostJobUseCase()
    private lateinit var profile: UserProfile

    private val _jobsUiModelState = MutableLiveData<JobsUiModel>()

    val jobsUiModelState: LiveData<JobsUiModel>
        get() = _jobsUiModelState

    fun getProfile() {
        viewModelScope.launch {
            profile = getProfileUseCase.getProfileLocal().toUserProfile()
            Log.d("PROF", "$profile")
            emitUiState(setUI = profile.enterprise)
        }
    }

    fun postJob(newJob: Job){
        emitUiState(showProgress = true)
        newJob.emailRecruiter = profile.email!!
        newJob.timestamp = Calendar.getInstance().time.toString()
        newJob.date = LocalDate().toString()
        newJob.time = LocalTime().toString()
        viewModelScope.launch(Dispatchers.IO) {
            val result = postJobUseCase.postJob(newJob)
            withContext(Dispatchers.Main){
                when(result) {
                    is JobsResult.Success -> postedSuccess()
                    is JobsResult.Error -> errorPosting(result.exception)
                }
            }
        }
    }

    private fun postedSuccess() {
        emitUiState(showProgress = false, showSuccess = Boolean.yes())
    }

    private fun errorPosting(exception: Exception) {
        emitUiState(showProgress = false, exception = exception)
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false, showSuccess: Boolean? = null) {
        val newJobUiModel = JobsUiModel(setUI, showProgress, enableContinueButton, exception, showSuccess)
        _jobsUiModelState.value = newJobUiModel
    }

}
