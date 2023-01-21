package mx.mauriciogs.ittalent.ui.jobs

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

class JobsViewModel : ViewModel() {

    private val _jobsUiModelState = MutableLiveData<JobsUiModel>()

    val jobsUiModelState: LiveData<JobsUiModel>
        get() = _jobsUiModelState



    private fun emitUiState(showProgress: Boolean = false,enableContinueButton: Boolean = false, exception: Exception? = null, showSuccess: Boolean? = null) {
        val signUpUiModel = JobsUiModel(showProgress, enableContinueButton, exception, showSuccess)
        _jobsUiModelState.value = signUpUiModel
    }

}
