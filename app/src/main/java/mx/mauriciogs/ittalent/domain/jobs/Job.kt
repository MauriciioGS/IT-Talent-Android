package mx.mauriciogs.ittalent.domain.jobs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import mx.mauriciogs.ittalent.core.extensions.default
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.data.jobs.model.JobFirebase

@Parcelize
data class Job (
    var id: String? = String.empty(),
    val job: String? = String.empty(),
    val enterprise: String? = String.empty(),
    val imageUrl: String? = String.empty(),
    val location: String? = String.empty(),
    val mode: String? = String.empty(),
    val type: String? = String.empty(),
    val wage: String? = String.empty(),
    val vacancies: String? = String.empty(),
    var applicants: List<String>? = emptyList(),
    var emailRecruiter: String? = String.empty(),
    var nameRecruiter: String? = String.empty(),
    var timestamp : String? = String.empty(),
    var date: String? = String.empty(),
    var time: String? = String.empty(),
    var status: Int? = Int.default(),
): Parcelable

fun Job.toJobFirebase() = JobFirebase(
    applicants = applicants?.ifEmpty { listOf("") },
    date = date,
    emailRecruiter = emailRecruiter,
    enterprise = enterprise,
    imageUrl = imageUrl?.ifBlank { "" },
    job = job,
    location = location,
    mode = mode,
    nameRecruiter = nameRecruiter,
    status = status,
    type = type,
    time = time,
    timestamp = timestamp,
    vacancies = vacancies,
    wage = wage,
)