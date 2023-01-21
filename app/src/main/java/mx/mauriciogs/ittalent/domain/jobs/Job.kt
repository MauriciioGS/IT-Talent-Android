package mx.mauriciogs.ittalent.domain.jobs

import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.data.jobs.model.JobFirebase

class Job (
    val job: String = String.empty(),
    val enterprise: String = String.empty(),
    val imageUrl: String = String.empty(),
    val location: String = String.empty(),
    val mode: String = String.empty(),
    val type: String = String.empty(),
    val wage: String = String.empty(),
    val vacancies: String = String.empty(),
    val applicants: List<String> = emptyList(),
    var emailRecruiter: String = String.empty(),
    var timestamp : String = String.empty(),
)

fun Job.toJobFirebase() = JobFirebase(
    applicants = applicants.ifEmpty { listOf("") },
    emailRecruiter = emailRecruiter,
    enterprise = enterprise,
    imageUrl = imageUrl.ifBlank { "" },
    job = job,
    location = location,
    mode = mode,
    type = type,
    timestamp = timestamp,
    vacancies = vacancies,
    wage = wage,
)