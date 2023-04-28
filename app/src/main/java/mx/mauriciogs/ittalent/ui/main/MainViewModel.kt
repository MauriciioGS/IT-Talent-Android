package mx.mauriciogs.ittalent.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.data.useraccount.local.entities.toUserProfile
import mx.mauriciogs.ittalent.domain.authentication.GetProfileUseCase
import mx.mauriciogs.ittalent.domain.notification.TokenUseCase
import javax.inject.Inject
import mx.mauriciogs.ittalent.data.notification.Result
import mx.mauriciogs.ittalent.data.notification.worker.UpdateFCMTokenWorker
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import java.util.concurrent.TimeUnit

fun <T : Any> Result<T>.success() = (this as? Result.Success)?.data

private const val MONTHLY_TOKEN_UPDATE_WORK = "MONTHLY_TOKEN_UPDATE_WORK"

@HiltViewModel
class MainViewModel @Inject constructor(private val getProfileUseCase: GetProfileUseCase,
                                        private val tokenUseCase: TokenUseCase,
                                        private val application: Application): ViewModel() {

    private val workManager by lazy {
        WorkManager.getInstance(application.applicationContext)
    }

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
                checkUpdateFCMToken(profile.email)
            }
        }
    }

    private fun checkUpdateFCMToken(email: String?) {
        val constraints = createConstraintsForWorker()
        val inputData = Data.Builder().apply { putString(UpdateFCMTokenWorker.KEY_USER_ID, email) }

        val saveTknRequest = PeriodicWorkRequestBuilder<UpdateFCMTokenWorker>(20, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, WorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .setInputData(inputData.build())
            .build()

        workManager.enqueueUniquePeriodicWork(MONTHLY_TOKEN_UPDATE_WORK, ExistingPeriodicWorkPolicy.KEEP, saveTknRequest).result.get()
    }

    private fun createConstraintsForWorker() = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
}
