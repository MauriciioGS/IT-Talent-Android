package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.data.jobs.JobsRepository

class NewApplicantUseCase {

    private val jobsRepository = JobsRepository()

    suspend fun addNewApplicant(applicants: List<String>, idDoc: String) = jobsRepository.addNewApplicantToJob(applicants, idDoc)
}
