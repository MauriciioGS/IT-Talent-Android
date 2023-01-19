package mx.mauriciogs.ittalent.data.auth.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirebaseClient {
    val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    val db = Firebase.firestore
}