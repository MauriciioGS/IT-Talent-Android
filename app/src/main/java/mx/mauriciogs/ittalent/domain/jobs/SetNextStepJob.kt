package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.core.extensions.yes
import mx.mauriciogs.ittalent.data.jobs.JobsRepository
import mx.mauriciogs.ittalent.data.jobs.exception.JobsException
import mx.mauriciogs.ittalent.data.jobs.model.JobsResult

class SetNextStepJob {
    private val jobsRepository = JobsRepository()

    suspend fun updateStatusJob(job: Job): JobsResult<Boolean> {
        return if(jobsRepository.updateStatusJob(job.status!!, job.id!!))
            JobsResult.Success(Boolean.yes())
        else
            JobsResult.Error(JobsException.UnrecognizedError)
    }
}
