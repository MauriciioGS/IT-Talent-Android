package mx.mauriciogs.ittalent.ui.talent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.talent.exceptions.TalentExceptionHandler
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.talent.GetTalentUseCase
import mx.mauriciogs.ittalent.domain.talent.Talent

class TalentViewModel: ViewModel() {

    private val getTalentUseCase = GetTalentUseCase()

    private var listOfUsers = mutableListOf<Talent>()

    private var _talentUiModelState = MutableLiveData<TalentUiModel>()

    val talentUiModelState: LiveData<TalentUiModel>
        get() = _talentUiModelState


    fun getTalent() {
        emitUiState(showProgress = true)
        viewModelScope.launch(Dispatchers.IO){
            val result = getTalentUseCase.getAllTalent()
            withContext(Dispatchers.Default) {
                when (result) {
                    is JobsResult.Success -> showSuccess(result.data.documents)
                    is JobsResult.Error -> showError(result.exception)
                }
            }
        }
    }

    private suspend fun showError(exception: Exception) = withContext(Dispatchers.Main) {
        emitUiState( showProgress = false, exception = TalentExceptionHandler.TalentEmpyList)
    }

    private suspend fun showSuccess(users: MutableList<DocumentSnapshot>) {
        users.forEach { document ->
            document.toObject<Talent>()?.let {
                listOfUsers.add(it)
            }
        }
        withContext(Dispatchers.Main) {
            if (listOfUsers.isNotEmpty()) emitUiState(showProgress = false, showSuccess = Boolean.yes())
            else emitUiState(showProgress = false, exception = TalentExceptionHandler.TalentEmpyList)
        }
    }

    fun filterByRole(role: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val filterList = mutableListOf<Talent>()
            listOfUsers.forEach { talent -> if (talent.profRole == role) filterList.add(talent) }
            withContext(Dispatchers.Main) {
                if (filterList.isNotEmpty()) emitUiState(showProgress = false, showSuccessFiler = filterList)
                else emitUiState(showProgress = false, exception = TalentExceptionHandler.TalentEmpyList)
            }
        }
    }

    private fun emitUiState(setUI: String? = null, showProgress: Boolean = false, exception: Exception? = null,
                            enableContinueButton: Boolean = false, showSuccess: Boolean? = null,
                            showSuccessFiler: MutableList<Talent>? = null) {
        val talentUiModel = TalentUiModel(setUI, showProgress, enableContinueButton, exception, showSuccess, showSuccessFiler)
        _talentUiModelState.value = talentUiModel
    }
}