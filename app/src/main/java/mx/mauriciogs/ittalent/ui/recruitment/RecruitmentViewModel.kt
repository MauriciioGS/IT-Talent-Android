package mx.mauriciogs.ittalent.ui.recruitment

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
import mx.mauriciogs.ittalent.data.jobs.exception.RecruitmentException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetAllJobsUseCase
import mx.mauriciogs.ittalent.domain.jobs.GetJobsPostedUseCase
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.jobs.SetNextStepJob
import mx.mauriciogs.ittalent.domain.talent.GetTalentUseCase
import mx.mauriciogs.ittalent.domain.talent.Talent
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile
import javax.inject.Inject

private const val PROCESS_JOB_STAGE1 = 0
private const val PROCESS_JOB_STAGE2 = 1
private const val PROCESS_JOB_STAGE3 = 2
private const val PROCESS_JOB_FINISHED = 4

@HiltViewModel
class RecruitmentViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase) :
    ViewModel() {

    private val getJobsPostedUseCase = GetJobsPostedUseCase()
    private val setNextStepJob = SetNextStepJob()
    private val getAllJobsUseCase = GetAllJobsUseCase()
    private val getTalentUseCase = GetTalentUseCase()

    private lateinit var profile: UserProfile
    private lateinit var jobSelected: Job

    private var _recruitmentUiModelState = MutableLiveData<RecruitmentUiModel>()
    val recruitmentUiModelState: LiveData<RecruitmentUiModel>
        get() = _recruitmentUiModelState

    private var _recruitmentFinishUiModelState = MutableLiveData<RecruitmentFinishUiModel>()
    val recruitmentFinishUiModelState: LiveData<RecruitmentFinishUiModel>
        get() = _recruitmentFinishUiModelState

    fun getProfile() {
        emitUiState(showProgress = true)
        viewModelScope.launch {
            val profLocal = getProfileUseCase.getProfileLocal()
            if (profLocal !== null) {
                profile = profLocal.toUserProfile()
                getMyJobPosts()
            }
        }
    }

    private fun getMyJobPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (profile.email != null) {
                val result = getJobsPostedUseCase.getJobsByRecruiter(profile.email!!)
                withContext(Dispatchers.Default) {
                    when (result) {
                        is JobsResult.Success -> {
                            getJobs(result.data.documents)
                        }
                        is JobsResult.Error -> showErrorGettingPosts(result.exception)
                    }
                }
            }
        }
    }

    private suspend fun getJobs(listPostFirebase: MutableList<DocumentSnapshot>) {
        var stepOneJobs = mutableListOf<Job>()
        var stepTwoJobs = mutableListOf<Job>()
        var stepThreeJobs = mutableListOf<Job>()
        var stepFourJobs = mutableListOf<Job>()
        listPostFirebase.forEach { document ->
            document.toObject<Job>()?.let {
                when (it.status) {
                    PROCESS_JOB_STAGE1 -> {
                        it.id = document.id
                        stepOneJobs.add(it)
                    }
                    PROCESS_JOB_STAGE2 -> {
                        it.id = document.id
                        stepTwoJobs.add(it)
                    }
                    PROCESS_JOB_STAGE3 -> {
                        it.id = document.id
                        stepThreeJobs.add(it)
                    }
                    PROCESS_JOB_FINISHED -> {
                        it.id = document.id
                        stepFourJobs.add(it)
                    }
                }
            }
        }
        withContext(Dispatchers.Main) {
            if (stepOneJobs.isNotEmpty()) emitUiState(
                showProgress = false,
                showSuccessStepOne = stepOneJobs
            )
            else emitUiState(showProgress = false, exception = RecruitmentException.EmptyListStage1)
            if (stepTwoJobs.isNotEmpty()) emitUiState(
                showProgress = false,
                showSuccessStepTwo = stepTwoJobs
            )
            else emitUiState(showProgress = false, exception = RecruitmentException.EmptyListStage2)
            if (stepThreeJobs.isNotEmpty()) emitUiState(
                showProgress = false,
                showSuccessStepThree = stepThreeJobs
            )
            else emitUiState(showProgress = false, exception = RecruitmentException.EmptyListStage3)
            if (stepFourJobs.isNotEmpty()) emitUiState(
                showProgress = false,
                showSuccessStepFour = stepFourJobs
            )
            else emitUiState(showProgress = false, exception = RecruitmentException.EmptyListStage4)
        }
    }

    private suspend fun showErrorGettingPosts(exception: Exception) =
        withContext(Dispatchers.Main) {
            emitUiState(showProgress = false, exception = exception)
        }

    fun setNextStep(job: Job) {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            if (job.status == PROCESS_JOB_STAGE3)
                job.status = PROCESS_JOB_FINISHED
            else
                job.status = job.status?.plus(1)
            Log.d("STATUS", "${job.status}")
            val result = setNextStepJob.updateStatusJob(job)
            withContext(Dispatchers.Main) {
                when (result) {
                    is JobsResult.Success -> {
                        if (job.status == PROCESS_JOB_FINISHED){
                            emitUiState(showProgress = false, showSuccessUpdate = true)
                            emitUiStateFinished(jobSelected.applicants, job)
                        } else emitUiState(showProgress = false, showSuccessUpdate = true)
                    }
                    is JobsResult.Error ->
                        emitUiState(showProgress = false, exception = RecruitmentException.UnrecognizedError2)
                }
            }
        }
    }

    fun getApplicants(id: String) {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = getAllJobsUseCase.getJobsById(id)
            when (result) {
                is JobsResult.Success -> getTalentApplicants(result.data.documents)
                is JobsResult.Error ->
                    emitUiState(showProgress = false, exception = RecruitmentException.UnrecognizedError2)
            }
        }
    }

    private fun getTalentApplicants(document: DocumentSnapshot) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTalentUseCase.getAllTalent()
            when (result) {
                is JobsResult.Success ->
                    filterTalentByJob(result.data.documents, document.toObject<Job>(), document.id)
                is JobsResult.Error ->
                    emitUiState(showProgress = false, exception = RecruitmentException.UnrecognizedError2)
            }
        }
    }

    private fun filterTalentByJob(documents: MutableList<DocumentSnapshot>, job: Job?, id: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (job != null) {
                jobSelected = job
                jobSelected.id = id
            }
            val listOfApplicants = mutableListOf<Talent>()

            documents.forEach { document ->
                document.toObject<Talent>()?.let { talent ->
                    if (job != null) {
                        if (job.applicants?.contains(talent.email) == true) {
                            listOfApplicants.add(talent)
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (listOfApplicants.isNotEmpty())
                    emitUiState(showProgress = false, showSuccessGetApplicants = listOfApplicants)
                else emitUiState(showProgress = false, exception = RecruitmentException.EmptyListApplicants)
            }
        }
    }

    fun setApplicantsNextStep(talentSelected: MutableList<Talent>, jobId: String) {
        //emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.Default) {
            if (jobSelected.id == jobId) {
                val talentEmails = mutableListOf("")
                talentSelected.forEach { talent ->
                    talent.email?.let { talentEmails.add(it) }
                }
                jobSelected.applicants = talentEmails
                withContext(Dispatchers.Main) {
                    when (setNextStepJob.updateApplicantsJob(jobSelected)) {
                        is JobsResult.Success -> { setNextStep(jobSelected) }
                        is JobsResult.Error ->
                            emitUiState(showProgress = false, exception = RecruitmentException.UnrecognizedError)
                    }
                }
            } else
                emitUiState(showProgress = false, exception = RecruitmentException.UnrecognizedError)
        }
    }

    fun stopFinishRecruitment(){
        emitUiStateFinished()
    }

    private fun emitUiState(
        showProgress: Boolean = false, exception: Exception? = null,
        showSuccessStepOne: MutableList<Job>? = null,
        showSuccessStepTwo: MutableList<Job>? = null,
        showSuccessStepThree: MutableList<Job>? = null,
        showSuccessStepFour: MutableList<Job>? = null,
        showSuccessUpdate: Boolean? = null,
        showSuccessGetApplicants: MutableList<Talent>? = null
    ) {
        val getJobsTalentUiModel = RecruitmentUiModel(
            showProgress,
            exception,
            showSuccessStepOne,
            showSuccessStepTwo,
            showSuccessStepThree,
            showSuccessStepFour,
            showSuccessUpdate,
            showSuccessGetApplicants
        )
        _recruitmentUiModelState.value = getJobsTalentUiModel
    }

    private fun emitUiStateFinished(showSendEmail: List<String>? = null, job: Job? = null){
        val finishProcessJob = RecruitmentFinishUiModel(showSendEmail, job)
        _recruitmentFinishUiModelState.value = finishProcessJob
    }

}
