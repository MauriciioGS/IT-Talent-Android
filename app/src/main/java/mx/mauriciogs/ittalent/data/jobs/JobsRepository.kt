package mx.mauriciogs.ittalent.data.jobs

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import mx.mauriciogs.ittalent.data.jobs.model.GetJobResponse
import mx.mauriciogs.ittalent.data.jobs.model.GetJobsResponse
import mx.mauriciogs.ittalent.data.jobs.model.JobFirebase
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult

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

    suspend fun updateStatusJob(newStatus: Int, id: String): Boolean = kotlin.runCatching {
        try {
            FirebaseClient.db.collection("jobs")
                .document(id)
                .update("status", newStatus)
                .await()
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("UPDJOB", it) }
        }
    }.isSuccess

    suspend fun updateApplicantsJob(newApplicants: List<String>, id: String): Boolean = kotlin.runCatching {
        try {
            FirebaseClient.db.collection("jobs")
                .document(id)
                .update("applicants", newApplicants)
                .await()
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("UPDJOB", it) }
        }
    }.isSuccess

    suspend fun addNewApplicantToJob(applicants: List<String>, id: String): Boolean = kotlin.runCatching {
        try {
            FirebaseClient.db.collection("jobs")
                .document(id)
                .update("applicants", applicants)
                .await()
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("UPDJOB", it) }
        }
    }.isSuccess

    suspend fun getJobsByRecruiter(email: String): JobsResult<GetJobsResponse> = withContext(
        Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("jobs")
                .whereEqualTo("emailRecruiter", email)
                .get()
                .await()
            JobsResult.Success(GetJobsResponse(Boolean.yes(), result.documents))
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("GETMYJOBS", it) }
            JobsResult.Error(exception)
        }
    }

    suspend fun getAllJobs(): JobsResult<GetJobsResponse> = withContext(Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("jobs")
                .get()
                .await()
            if(result.isEmpty) Log.d("VACIA", "Lista vacia firebase")
            JobsResult.Success(GetJobsResponse(Boolean.yes(), result.documents))
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("GETALLJOBS", it) }
            JobsResult.Error(exception)
        }
    }

    suspend fun getJobById(id: String): JobsResult<GetJobResponse> = withContext(Dispatchers.IO) {
        try {
            val result = FirebaseClient.db.collection("jobs")
                .document(id)
                .get()
                .await()
            if(result.data.isNullOrEmpty()) Log.d("NOJOB", "No se encontro empleo")
            JobsResult.Success(GetJobResponse(Boolean.yes(), result))
        } catch (exception: Exception){
            exception.printStackTrace()
            exception.message?.let { Log.d("GETJOBBYID", it) }
            JobsResult.Error(exception)
        }
    }
}