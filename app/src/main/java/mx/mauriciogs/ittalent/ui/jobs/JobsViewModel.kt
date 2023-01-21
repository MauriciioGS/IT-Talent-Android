package mx.mauriciogs.ittalent.ui.jobs

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
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetJobsPostedUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase): ViewModel() {

    private val getJobsPostedUseCase = GetJobsPostedUseCase()

    private val _jobsUiModelState = MutableLiveData<GetJobsUiModel>()

    private lateinit var profile: UserProfile

    val jobsUiModelState: LiveData<GetJobsUiModel>
        get() = _jobsUiModelState

    fun getProfile() {
        viewModelScope.launch {
            profile = getProfileUseCase.getProfileLocal().toUserProfile()
            emitUiState(setUI = profile.email)
        }
    }

    fun getMyJobPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (profile.email != null) {
                val result = getJobsPostedUseCase.getJobsByRecruiter(profile.email!!)
                withContext(Dispatchers.Default) {
                    when (result) {
                        is JobsResult.Success -> { getPosts(result.data.documents)}
                        is JobsResult.Error -> showErrorGettingPosts(result.exception)
                    }
                }
            }
        }
    }

    private suspend fun getPosts(listPostFirebase: MutableList<DocumentSnapshot>) {
        var myPosts = mutableListOf<Job>()
        listPostFirebase.forEach { document ->
            document.toObject<Job>()?.let { myPosts.add(it) }
        }
        withContext(Dispatchers.Main) {
            if (myPosts.isNotEmpty()) emitUiState(showProgress = false, showSuccess = myPosts)
            else emitUiState(showProgress = false, exception = JobsException.EmptyListOfJobs)
        }

    }

    private suspend fun showErrorGettingPosts(exception: Exception) = withContext(Dispatchers.Main) {
        emitUiState(showProgress = false,exception = exception)
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false, showSuccess: MutableList<Job>? = null) {
        val newJobUiModel = GetJobsUiModel(setUI, showProgress, enableContinueButton, exception, showSuccess)
        _jobsUiModelState.value = newJobUiModel
    }

}
