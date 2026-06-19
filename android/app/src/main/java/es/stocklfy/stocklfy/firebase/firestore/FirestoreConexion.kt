package es.stocklfy.stocklfy.firebase.firestore

import android.annotation.SuppressLint
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreConexion {

    // Instancia única de Firestore
    @SuppressLint("StaticFieldLeak")
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
}