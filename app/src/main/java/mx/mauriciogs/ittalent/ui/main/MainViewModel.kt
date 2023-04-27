package mx.mauriciogs.ittalent.ui.main

import android.app.Application
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
import mx.mauriciogs.ittalent.domain.notification.TokenUseCase
import javax.inject.Inject
import mx.mauriciogs.ittalent.data.notification.Result

fun <T : Any> Result<T>.success() = (this as? Result.Success)?.data

@HiltViewModel
class MainViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase,
                                        private val tokenUseCase: TokenUseCase,
                                        private val application: Application): ViewModel() {

    private var _initUI = MutableLiveData<Boolean>()
    val initUI: LiveData<Boolean>
        get() = _initUI

    private var _error = MutableLiveData<Exception?>()
    val error: LiveData<Exception?>
        get() = _error

    fun checkToken() {
        viewModelScope.launch(Dispatchers.IO) {
            val profileLocal = getProfileUseCase.getProfileLocal()
            if (profileLocal != null) {
                val profile = profileLocal.toUserProfile()
                SharedPreferences.init(application)
                val preferences = SharedPreferences.instance
                val tokenStored = preferences?.fcmToken

                val registerToken = tokenUseCase.createToken().success().orEmpty()

                if (tokenStored != null && tokenStored.isBlank() || tokenStored != registerToken) {
                    val result = tokenUseCase.saveToken(profile.email!!, registerToken)
                    preferences?.putFcmToken(registerToken)
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is Result.Success -> _initUI.value = true
                            is Result.Error -> _error.value = result.exception
                        }


                    }
                }
            }
        }
    }
}