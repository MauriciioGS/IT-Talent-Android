package mx.mauriciogs.ittalent.ui.postulations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.data.jobs.exception.ApplyJobException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetAllJobsUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.ui.jobs.GetJobsTalentUiModel
import javax.inject.Inject

@HiltViewModel
class PostulationsTalViewModel@Inject constructor(private val getProfileUseCase: GetProfileUseCase): ViewModel() {

    private val getAllJobsUseCase = GetAllJobsUseCase()

    private var email = String.empty()

    private var _postTalUiModelState = MutableLiveData<GetJobsTalentUiModel>()
    val postTalUiModelState: LiveData<GetJobsTalentUiModel>
        get() = _postTalUiModelState

    fun getProfile() {
        viewModelScope.launch {
            email = getProfileUseCase.getProfileLocal().toUserProfile().email!!
            getJobs()
        }
    }

    private fun getJobs() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getAllJobsUseCase.getAllJobs()
            withContext(Dispatchers.Default) {
                when (result) {
                    is JobsResult.Success -> { filterJobsByUser(result.data.documents) }
                    is JobsResult.Error -> showErrorGettingPosts(result.exception)
                }
            }
        }
    }

    private fun filterJobsByUser(jobsFirebase: MutableList<DocumentSnapshot>) {
        viewModelScope.launch(Dispatchers.Default) {
            val jobsByApplicant = mutableListOf<Job>()
            jobsFirebase.forEach { document ->
                document.toObject<Job>()?.let { job ->
                    job.applicants?.forEach { applicant ->
                        if (applicant == email) jobsByApplicant.add(job)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (jobsByApplicant.isNotEmpty()) emitUiTalentState(showProgress = false, setUI = jobsByApplicant)
                else emitUiTalentState(showProgress = false, setUI = jobsByApplicant, exception = ApplyJobException.EmptyListOfApplyJobs)
            }
        }
    }

    private suspend fun showErrorGettingPosts(exception: Exception) = withContext(Dispatchers.Main) {
        emitUiTalentState(showProgress = false,exception = exception)
    }

    private fun emitUiTalentState(setUI: MutableList<Job>? = null, showProgress: Boolean = false, exception: Exception? = null,
                                  showSuccessNewFilter: MutableList<Job>? = null) {
        val getJobsTalentUiModel = GetJobsTalentUiModel(setUI, showProgress, exception, showSuccessNewFilter)
        _postTalUiModelState.value = getJobsTalentUiModel
    }
}