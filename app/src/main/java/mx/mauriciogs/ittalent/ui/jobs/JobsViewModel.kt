package mx.mauriciogs.ittalent.ui.jobs

import android.util.Log
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
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetAllJobsUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetJobsPostedUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import javax.inject.Inject

private const val PROCESS_JOB_STAGE1 = 0

@HiltViewModel
class JobsViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase): ViewModel() {

    private val getJobsPostedUseCase = GetJobsPostedUseCase()
    private val getAllJobsUseCase = GetAllJobsUseCase()

    lateinit var profile: UserProfile

    private val _jobsUiModelState = MutableLiveData<GetJobsUiModel>()
    val jobsUiModelState: LiveData<GetJobsUiModel>
        get() = _jobsUiModelState

    private val _jobsUiTalentModelState = MutableLiveData<GetJobsTalentUiModel>()
    val jobsUiTalentModelState: LiveData<GetJobsTalentUiModel>
        get() = _jobsUiTalentModelState



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
        var myActivePosts = mutableListOf<Job>()
        var myPastPosts = mutableListOf<Job>()
        listPostFirebase.forEach { document ->
            document.toObject<Job>()?.let {
                if (it.status != 4) myActivePosts.add(it)
                else myPastPosts.add(it)
            }
        }
        withContext(Dispatchers.Main) {
            if (myActivePosts.isNotEmpty()) emitUiState(showProgress = false, showSuccessActivePosts = myActivePosts)
            else emitUiState(showProgress = false, exception = JobsException.EmptyListOfAciveJobs)
            if (myPastPosts.isNotEmpty()) emitUiState(showProgress = false, showSuccessPastJobs = myPastPosts)
            else emitUiState(showProgress = false, exception = JobsException.EmptyListOfPastJobs)
        }

    }

    private suspend fun showErrorGettingPosts(exception: Exception) = withContext(Dispatchers.Main) {
        emitUiState(showProgress = false,exception = exception)
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null,
                            enableContinueButton: Boolean = false, showSuccessActivePosts: MutableList<Job>? = null,
                            showSuccessPastJobs: MutableList<Job>? = null
    ) {
        val newJobUiModel = GetJobsUiModel(setUI, showProgress, enableContinueButton, exception, showSuccessActivePosts, showSuccessPastJobs)
        _jobsUiModelState.value = newJobUiModel
    }

    fun getProfileTalent() {
        emitUiTalentState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val profLocal = getProfileUseCase.getProfileLocal().toUserProfile()
            val result = getProfileUseCase.getProfileFirebaseByEmail(profLocal.email!!)
            withContext(Dispatchers.Main){
                when(result){
                    is AuthResult.Success -> {
                        profile = result.data.user!!
                        Log.d("PROFTAL", "$profile")
                        getJobs()
                    }
                    is AuthResult.Error -> emitUiState(showProgress = false, exception = result.exception)
                }
            }
        }
    }

    private fun getJobs() {
        viewModelScope.launch(Dispatchers.IO) {
            if (profile.profRole != null) {
                val result = getAllJobsUseCase.getAllJobs()
                withContext(Dispatchers.Default) {
                    when (result) {
                        is JobsResult.Success -> { filterJobsByRole(result.data.documents) }
                        is JobsResult.Error -> showErrorGettingPosts(result.exception)
                    }
                }
            }
        }
    }

    private fun filterJobsByRole(jobsFirebase: MutableList<DocumentSnapshot>) {
        viewModelScope.launch(Dispatchers.Default) {
            val jobsByRole = mutableListOf<Job>()
            val role = profile.profRole!!.split(" ")
            jobsFirebase.forEach { document ->
                document.toObject<Job>()?.let {
                    // Filtra por rol: por ejemplo si soy Android Developer encuentra "Android" y "Developer"
                    role.forEach { trim ->
                        if (it.job?.contains(trim) == true && it.status == PROCESS_JOB_STAGE1) {
                            if (it.applicants?.contains(profile.email!!) == false){
                                it.id = document.id
                                jobsByRole.add(it)
                            }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (jobsByRole.isNotEmpty()) emitUiTalentState(showProgress = false, setUI = jobsByRole)
                else emitUiTalentState(showProgress = false, setUI = jobsByRole, exception = JobsException.EmptyListOfFilteredJobs)
            }
        }
    }

    private fun emitUiTalentState(setUI: MutableList<Job>? = null, showProgress: Boolean = false, exception: Exception? = null,
                             showSuccessNewFilter: MutableList<Job>? = null) {
        val getJobsTalentUiModel = GetJobsTalentUiModel(setUI, showProgress, exception, showSuccessNewFilter)
        _jobsUiTalentModelState.value = getJobsTalentUiModel
    }

}
