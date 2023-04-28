package mx.mauriciogs.ittalent.data.notification

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import mx.mauriciogs.ittalent.data.auth.remote.FirebaseClient
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Instant
import org.joda.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class Token(var token: String? = null, var timestamp: String? = null)

class TokenNotGottenException(override val message: String = "Error: token hasn't been gotten") : Exception(message)

class TokenNotCreatedException(override val message: String = "Error: token not created") : Exception(message)


class TokenRemoteDataSource @Inject constructor(){

    private var mCollection: CollectionReference = FirebaseClient.db.collection("Tokens")

    suspend fun createToken(): Result<String> = suspendCoroutine { continuation ->
        Firebase.messaging.token.addOnCompleteListener { task: Task<String?> ->
            val newToken = Token(task.result, DateTime.now().withZone(DateTimeZone.forID("America/Mexico_City")).toString("yyyy-MM-dd HH:mm:ss")).token.orEmpty()
            continuation.resume(Result.Success(newToken))
        }
    }

    suspend fun saveToken(idUser: String, tokenString: String): Result<Boolean> = suspendCoroutine { continuation ->
        mCollection.document(idUser).set(Token(tokenString, DateTime.now().withZone(DateTimeZone.forID("America/Mexico_City")).toString("yyyy-MM-dd HH:mm:ss"))).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) continuation.resume(Result.Success(task.isSuccessful))
            else continuation.resume(Result.Error(TokenNotCreatedException()))
        }
    }

    suspend fun getToken(idUser: String): Result<DocumentSnapshot> = suspendCoroutine { continuation ->  //getUserRealtime
        mCollection.document(idUser).get().addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
            if (documentSnapshot.exists())
                continuation.resume(Result.Success(documentSnapshot)) else continuation.resume(Result.Error(TokenNotGottenException()))
        }
    }
}