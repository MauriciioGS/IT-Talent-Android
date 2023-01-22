package mx.mauriciogs.ittalent.data.talent

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.TALENT_UT
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.data.jobs.model.GetJobsResponse
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult

class TalentRepository {
    suspend fun getAllTalent(): JobsResult<GetJobsResponse> = withContext(
        Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("users")
                .whereEqualTo("userType", Int.TALENT_UT())
                .get()
                .await()
            JobsResult.Success(GetJobsResponse(Boolean.yes(), result.documents))
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("GETTALENT", it) }
            JobsResult.Error(exception)
        }
    }
}