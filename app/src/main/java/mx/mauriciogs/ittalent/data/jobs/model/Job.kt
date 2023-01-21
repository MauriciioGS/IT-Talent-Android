package mx.mauriciogs.ittalent.data.jobs.model

import mx.mauriciogs.ittalent.domain.jobs.Job

data class JobFirebase (
        val job: String? = null,
        val enterprise: String? = null,
        val imageUrl: String? = null,
        val location: String? = null,
        val mode: String? = null,
        val type: String? = null,
        val wage: String? = null,
        val vacancies: String? = null,
        val applicants: List<String>? = null,
        val emailRecruiter: String? = null,
        val nameRecruiter: String? = null,
        val timestamp : String? = null,
        val date: String? = null,
        val time: String? = null,
        var status: Int? = null,
)

fun JobFirebase.toJob() = Job(
        applicants = applicants!!,
        date = date!!,
        emailRecruiter = emailRecruiter!!,
        enterprise = enterprise!!,
        imageUrl = imageUrl!!,
        job = job!!,
        location = location!!,
        mode = mode!!,
        nameRecruiter = nameRecruiter!!,
        type = type!!,
        time = time!!,
        timestamp = timestamp!!,
        vacancies = vacancies!!,
        wage = wage!!,
        status = status!!
)