package es.stocklfy.stocklfy.firebase

import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth

class AuthFirebase {

    private val auth = FirebaseAuth.getInstance()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser

                    if (user != null) {
                        val uid = user.uid
                        val emailUser = user.email ?: ""
                        onSuccess(uid, emailUser)
                    } else {
                        onError("Error obteniendo usuario")
                    }
                } else {
                    onError(task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}

