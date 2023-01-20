package mx.mauriciogs.ittalent.data.jobs

import android.util.Log
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.model.AuthResult
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.data.jobs.model.JobFirebase
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult
import mx.mauriciogs.ittalent.data.useraccount.models.GetProfileFirebaseResponse
import mx.mauriciogs.ittalent.domain.jobs.Job
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

class JobsRepository {

    suspend fun addJob(newJob: JobFirebase): Boolean = kotlin.runCatching {
        try {
            FirebaseClient.db.collection("jobs")
                .add(newJob)
                .await()
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("ADDJOB", it) }
        }
    }.isSuccess

    suspend fun getJobsByRecruiter(email: String): JobsResult<GetProfileFirebaseResponse> = withContext(
        Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("jobs")
                .document(email)
                .get()
                .await()
            JobsResult.Success(GetProfileFirebaseResponse(Boolean.yes(), result.toObject<UserProfile>()))
        } catch (exception: Exception){
            JobsResult.Error(exception)
        }
    }
}