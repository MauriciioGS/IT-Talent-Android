package mx.mauriciogs.ittalent.ui.authentication.signup.util

import mx.mauriciogs.ittalent.ui.global.extensions.default
import mx.mauriciogs.ittalent.ui.global.extensions.empty
import mx.mauriciogs.ittalent.ui.global.extensions.no

data class UserSignUpCredentials(
    // Talent
    var userType: Int = Int.default(),
    var email: String = String.empty(),
    var password: String = String.empty(),
    var fullName: String = String.empty(),
    var country: String = String.empty(),
    var city: String = String.empty(),
    var age: Int = Int.default(),
    var phoneNumber: String = String.empty(),
    var resume: String = String.empty(),
    var profRole: String = String.empty(),
    var photoUri: String = String.empty(),
    var xpLevel: String = String.empty(),
    var skills: List<String>? = null,
    var experiences: MutableList<Experience> = mutableListOf(),

    // Recruit
    var enterprise: Enterprise? = null,
    var role: String = String.empty(),

    var store: String = String.empty()) {

    companion object {
        fun empty() = UserSignUpCredentials(
            store = "Google Play"
        )
    }
}

data class Enterprise(
    var name: String = String.empty(),
    var urlImage: String = String.empty(),
    var country: String = String.empty(),
)

data class Experience(
    var charge: String = String.empty(),
    var enterprise: String,
    var city: String = String.empty(),
    var mode: String = String.empty(),
    var type: String = String.empty(),
    var period: String = String.empty(),
    var yearsXp: Int = Int.default(),
    var nowadays: Boolean = Boolean.no(),
    var achievements: String = String.empty()
)