package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.data.jobs.JobsRepository

class GetAllJobsUseCase {

    private val jobsRepository = JobsRepository()

    suspend fun getAllJobs() = jobsRepository.getAllJobs()
}