package mx.mauriciogs.ittalent.data.jobs.model

sealed class JobsResult<out T : Any> {
    data class Success<out T : Any>(val data: T) : JobsResult<T>()
    data class Error(val exception: Exception) : JobsResult<Nothing>()
}