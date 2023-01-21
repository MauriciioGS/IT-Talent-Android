package mx.mauriciogs.ittalent.ui.jobs

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
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase): ViewModel() {

    private val _jobsUiModelState = MutableLiveData<JobsUiModel>()

    private lateinit var profile: UserProfile

    val jobsUiModelState: LiveData<JobsUiModel>
        get() = _jobsUiModelState

    fun getProfile() {
        viewModelScope.launch {
            profile = getProfileUseCase.getProfileLocal().toUserProfile()
            emitUiState(setUI = profile.email)
        }
    }

    fun getMyJobPosts() {

    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null, enableContinueButton: Boolean = false, showSuccess: Boolean? = null) {
        val newJobUiModel = JobsUiModel(setUI, showProgress, enableContinueButton, exception, showSuccess)
        _jobsUiModelState.value = newJobUiModel
    }

}
