package mx.mauriciogs.ittalent.domain.talent

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import mx.mauriciogs.ittalent.core.extensions.default
import mx.mauriciogs.ittalent.core.extensions.empty
import mx.mauriciogs.ittalent.core.extensions.no

@Parcelize
data class Talent(
    // Talent
    var userType: Int? = Int.default(),
    var email: String? = String.empty(),
    var password: String? = String.empty(),
    var fullName: String? = String.empty(),
    var country: String? = String.empty(),
    var city: String? = String.empty(),
    var age: Int? = Int.default(),
    var phoneNum: String? = String.empty(),
    var resume: String? = String.empty(),
    var profRole: String? = String.empty(),
    var photoUrl: String? = String.empty(),
    var xpLevel: String? = String.empty(),
    var skills: List<String>? = null,
    var experiences: List<Experience>? = null,

    // Recruit
    var enterprise: String? = String.empty(),
    var role: String? = String.empty(),
    var store: String? = String.empty()
): Parcelable

@Parcelize
data class Experience(
    var charge: String? = String.empty(),
    var enterprise: String? = String.empty(),
    var city: String? = String.empty(),
    var mode: String? = String.empty(),
    var type: String? = String.empty(),
    var period: String? = String.empty(),
    var yearsXp: Int? = Int.default(),
    var nowadays: Boolean? = Boolean.no(),
    var achievements: String? = String.empty()
): Parcelable