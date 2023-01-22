package mx.mauriciogs.ittalent.domain.useraccount.profile

import android.provider.ContactsContract.CommonDataKinds.Email
import mx.mauriciogs.ittalent.data.useraccount.remote.UserRepositoryRemote
import mx.mauriciogs.ittalent.domain.useraccount.UserProfile

class UpdateProfileRecruiterUseCase {

    private val userRepositoryRemote = UserRepositoryRemote()

    suspend fun updateProfileRecruiter(userProfile: UserProfile) = userRepositoryRemote.updateProfileRecruiterFirebase(userProfile)

}