package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.data.jobs.JobsRepository

class GetJobsPostedUseCase {

    private val jobsRepository = JobsRepository()

    suspend fun getJobsByRecruiter(email: String) = jobsRepository.getJobsByRecruiter(email)

}
