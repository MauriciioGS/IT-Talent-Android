package mx.mauriciogs.ittalent.domain.useraccount

data class UserProfile(
    // Talent
    var userType: Int? = null,
    var email: String? = null,
    var password: String? = null,
    var fullName: String? = null,
    var country: String? = null,
    var city: String? = null,
    var age: Int? = null,
    var phoneNumber: String? = null,
    var resume: String? = null,
    var profRole: String? = null,
    var photoUri: String? = null,
    var xpLevel: String? = null,
    var skills: List<String>? = null,
    var experiences: List<Experience>? = null,

    // Recruit
    var enterprise: String? = null,
    var role: String? = null,
    var store: String? = null
)

data class Experience(
    var charge: String? = null,
    var enterprise: String? = null,
    var city: String? = null,
    var mode: String? = null,
    var type: String? = null,
    var period: String? = null,
    var yearsXp: Int? = null,
    var nowadays: Boolean? = null,
    var achievements: String ?= null
)