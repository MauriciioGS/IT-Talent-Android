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
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.jobs.PostJobUseCase
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import org.joda.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class NewJobViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) : ViewModel() {

    private val postJobUseCase = PostJobUseCase()
    private lateinit var profile: UserProfile

    private val _jobsUiModelState = MutableLiveData<NewJobUiModel>()

    val jobsUiModelState: LiveData<NewJobUiModel>
        get() = _jobsUiModelState

    fun getProfile() {
        emitUiState(showProgress = true)
        viewModelScope.launch {
            profile = getProfileUseCase.getProfileLocal().toUserProfile()
            Log.d("PROF", "$profile")
            emitUiState(setUI = profile.enterprise, showProgress = false)
        }
    }

    fun postJob(newJob: Job){
        newJob.emailRecruiter = profile.email!!
        newJob.timestamp = LocalDate().toString()
        Log.d("POSTJOB", "$newJob")
        /*viewModelScope.launch(Dispatchers.IO) {
            val result = postJobUseCase.postJob(newJob)
        }*/
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false, showSuccess: Boolean? = null) {
        val newJobUiModel = NewJobUiModel(setUI, showProgress, enableContinueButton, exception, showSuccess)
        _jobsUiModelState.value = newJobUiModel
    }

}
