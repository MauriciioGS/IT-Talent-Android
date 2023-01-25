package mx.mauriciogs.ittalent.ui.see_all

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
class SeeAllItemsViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) : ViewModel() {

    private val getJobsPostedUseCase = GetJobsPostedUseCase()

    private lateinit var profile: UserProfile

    private val _seeAllUiModelState = MutableLiveData<SeeAllUiModel>()

    val seeAllUiModelState: LiveData<SeeAllUiModel>
        get() = _seeAllUiModelState

    var myPosts = mutableListOf<Job>()

    fun getProfile() {
        viewModelScope.launch {
            val profileLocal = getProfileUseCase.getProfileLocal()
            if (profileLocal != null) {
                profile = profileLocal.toUserProfile()
                emitUiState(setUI = profile.email)
            }
        }
    }

    fun getJobsByRecruiter(isActives: Boolean) {
        if (profile.email != null) {
            if (isActives){
                viewModelScope.launch(Dispatchers.IO) {
                    if (profile.email != null) {
                        val result = getJobsPostedUseCase.getJobsByRecruiter(profile.email!!)
                        withContext(Dispatchers.Default) {
                            when (result) {
                                is JobsResult.Success -> { getActivePosts(result.data.documents)}
                                is JobsResult.Error -> showError(result.exception)
                            }
                        }
                    }
                }
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    if (profile.email != null) {
                        val result = getJobsPostedUseCase.getJobsByRecruiter(profile.email!!)
                        withContext(Dispatchers.Default) {
                            when (result) {
                                is JobsResult.Success -> { getPastPosts(result.data.documents)}
                                is JobsResult.Error -> showError(result.exception)
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun getActivePosts(listPostFirebase: MutableList<DocumentSnapshot>) {
        myPosts.clear()
        listPostFirebase.forEach { document ->
            document.toObject<Job>()?.let {
                if (it.status != 4) myPosts.add(it)
            }
        }
        withContext(Dispatchers.Main) {
            if (myPosts.isNotEmpty()) emitUiState(showProgress = false, showSucess = true)
            else emitUiState(showProgress = false, exception = JobsException.EmptyListOfAciveJobs)
        }
    }

    private suspend fun getPastPosts(listPostFirebase: MutableList<DocumentSnapshot>) {
        myPosts.clear()
        listPostFirebase.forEach { document ->
            document.toObject<Job>()?.let {
                if (it.status == 4) myPosts.add(it)
            }
        }
        withContext(Dispatchers.Main) {
            if (myPosts.isNotEmpty()) emitUiState(showProgress = false, showSucess = true)
            else emitUiState(showProgress = false, exception = JobsException.EmptyListOfAciveJobs)
        }
    }

    private suspend fun showError(exception: Exception) = withContext(Dispatchers.Main) {
        emitUiState(showProgress = false, exception = JobsException.UnrecognizedError)
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null,
                            showSucess: Boolean? = null
    ) {
        val seeAllUiModel = SeeAllUiModel(setUI, showProgress, exception, showSucess)
        _seeAllUiModelState.value = seeAllUiModel
    }
}
