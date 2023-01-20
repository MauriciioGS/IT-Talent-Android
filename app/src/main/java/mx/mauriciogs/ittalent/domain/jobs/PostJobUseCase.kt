package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.jobs.JobsRepository
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult

class PostJobUseCase {

    private val jobsRepository = JobsRepository()

    suspend fun postJob(newJob: Job): JobsResult<Boolean>{
        return if(jobsRepository.addJob(newJob.toJobFirebase()))
            JobsResult.Success(Boolean.yes())
        else
            JobsResult.Error(JobsException.UnrecognizedError)
    }
}