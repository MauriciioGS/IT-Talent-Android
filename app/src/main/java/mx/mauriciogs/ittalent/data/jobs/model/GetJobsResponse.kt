package mx.mauriciogs.ittalent.data.jobs.model

import com.google.firebase.firestore.DocumentSnapshot
import mx.mauriciogs.ittalent.core.extensions.no

data class GetJobsResponse(
    val isSuccessfull: Boolean = Boolean.no(),
    val documents: MutableList<DocumentSnapshot>

    )