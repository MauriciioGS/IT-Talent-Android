package mx.mauriciogs.ittalent.data.notification.worker

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.data.notification.TokenRemoteDataSource
import mx.mauriciogs.ittalent.ui.main.success

@HiltWorker
class UpdateFCMTokenWorker @AssistedInject constructor(private val tokenRemoteDataSource: TokenRemoteDataSource,
                                                       @Assisted applicationContext: Context,
                                                       @Assisted workerParameters: WorkerParameters): CoroutineWorker(applicationContext, workerParameters) {

    private lateinit var sharedPreferences: SharedPreferences

    override suspend fun doWork(): Result {

        val userId = inputData.getString(KEY_USER_ID) ?: return Result.retry()

        return withContext(Dispatchers.IO) {
            try {

                Log.d("WORKER", "Guardando token desde el Worker")
                val newToken = tokenRemoteDataSource.createToken().success().orEmpty()
                tokenRemoteDataSource.saveToken(userId, newToken)

                sharedPreferences = applicationContext.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
                sharedPreferences.edit().putString(FCM_TOKEN, newToken).apply()

                Result.success(workDataOf(KEY_USER_ID to userId))

            } catch (exception: Exception) {
                exception.localizedMessage?.let { Log.e(TAG, it) }
                Result.failure()
            }
        }
    }

    companion object {

        const val TAG = "UpdateFCMTokenWorker"
        const val APP_NAME = "Shugar"
        const val KEY_USER_ID = "KEY_USER_ID"
        const val FCM_TOKEN = "FCM_TOKEN"
    }

}