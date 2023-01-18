package mx.mauriciogs.ittalent.ui.connectivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mx.mauriciogs.ittalent.core.extensions.yes

class LostConnViewModel: ViewModel() {

    private var _isUiEnabled = MutableLiveData<Boolean>()
    val isUiEnabled : LiveData<Boolean>
        get() = _isUiEnabled

    fun continueUI() {
        _isUiEnabled.value = Boolean.yes()
    }
}