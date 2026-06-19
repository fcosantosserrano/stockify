package es.stocklfy.stocklfy.firebase.firestore.opciones

import android.net.Uri
import es.stocklfy.stocklfy.firebase.firestore.FirestoreConexion

class UsuarioFirestore {

    fun guardarUsuario(uid: String, email: String) {

        val nombre = email.substringBefore("@")

        val usuario = hashMapOf(
            "uid" to uid,
            "email" to email,
            "nombre" to nombre,
            "fotoPerfil" to "",
            "notificacionesActivas" to true
        )

        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .set(usuario)
    }

    fun obtenerNombreUsuario(
        uid: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre") ?: "Usuario"
                    onSuccess(nombre)
                } else {
                    onError("Usuario no encontrado")
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al obtener usuario")
            }
    }
    fun actualizarNombre(
        uid: String,
        nuevoNombre: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        FirestoreConexion.db.collection("usuarios")
            .document(uid)
            .update("nombre", nuevoNombre)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al actualizar nombre")
            }
    }

    fun actualizarFotoPerfil(
        uid: String,
        urlFoto: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .document(uid)
            .update("fotoPerfil", urlFoto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Error guardando foto") }
    }

    /*fun subirFotoPerfil(
        uid: String,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("usuarios/$uid/perfil.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error subiendo imagen")
            }
    }*/
}