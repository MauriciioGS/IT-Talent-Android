package mx.mauriciogs.ittalent.data.useraccount.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExperienceItem (
    var charge: String,
    var enterprise: String,
    var city: String,
    var mode: String,
    var type: String,
    var period: String,
    var yearsXp: Int,
    var nowadays: Boolean,
    var achievements: String
): Parcelable