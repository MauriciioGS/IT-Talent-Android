package mx.mauriciogs.ittalent.ui.notification

import com.google.firebase.messaging.FirebaseMessagingService

class ITTalentFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println(token)
    }


}