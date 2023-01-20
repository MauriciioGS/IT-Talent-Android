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
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.jobs.PostJobUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) : ViewModel() {

    private val postJobUseCase = PostJobUseCase()
    private lateinit var profile: UserProfile

    private val _jobsUiModelState = MutableLiveData<JobsUiModel>()

    val jobsUiModelState: LiveData<JobsUiModel>
        get() = _jobsUiModelState

    fun getProfile() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                profile = getProfileUseCase.getProfileLocal()
                Log.d("PROF", "${profile.fullName}")
                withContext(Dispatchers.Main) { emitUiState(showProgress = false) }
            } catch (exception: Exception) {
                withContext(Dispatchers.Main) { emitUiState(exception = exception) }
            }
        }
    }

    fun postJob(newJob: Job){
        viewModelScope.launch(Dispatchers.IO) {
            val result = postJobUseCase.postJob(newJob)
        }
    }

    private fun emitUiState(showProgress: Boolean = false, exception: Exception? = null, showSuccess: Boolean? = null) {
        val signUpUiModel = JobsUiModel(showProgress, exception, showSuccess)
        _jobsUiModelState.value = signUpUiModel
    }

}
