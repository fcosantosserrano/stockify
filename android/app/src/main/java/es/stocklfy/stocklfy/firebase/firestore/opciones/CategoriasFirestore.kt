package es.stocklfy.stocklfy.firebase.firestore.opciones

import android.util.Log
import es.stocklfy.stocklfy.firebase.firestore.FirestoreConexion
import es.stocklfy.stocklfy.global.normalizarCategoria

class CategoriasFirestore {

    fun crearCategoriasPorDefecto(
        uid: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val categorias = listOf(
            mapOf("nombre" to "Lácteos", "icono" to "milk", "color" to "#A5D6A7", "orden" to 1),
            mapOf("nombre" to "Carnes", "icono" to "meat", "color" to "#EF9A9A", "orden" to 2),
            mapOf("nombre" to "Pescados", "icono" to "fish", "color" to "#90CAF9", "orden" to 3),
            mapOf("nombre" to "Frutas", "icono" to "fruit", "color" to "#FFCC80", "orden" to 4),
            mapOf("nombre" to "Verduras", "icono" to "vegetable", "color" to "#A5D6A7", "orden" to 5),
            mapOf("nombre" to "Bebidas", "icono" to "drink", "color" to "#80DEEA", "orden" to 6),
            mapOf("nombre" to "Panadería", "icono" to "bakery", "color" to "#D7CCC8", "orden" to 7),
            mapOf("nombre" to "Despensa", "icono" to "pantry", "color" to "#CE93D8", "orden" to 8),
            mapOf("nombre" to "Congelados", "icono" to "frozen", "color" to "#B3E5FC", "orden" to 9),
            mapOf("nombre" to "Otros", "icono" to "other", "color" to "#E0E0E0", "orden" to 10),
            hashMapOf("nombre" to "Refrescos","icono" to "drink","color" to "#29B6F6","orden" to 10)
        )

        val batch = FirestoreConexion.db.batch()

        categorias.forEach { categoria ->
            val nombreDocumento = normalizarCategoria(categoria["nombre"].toString())

            val referencia = FirestoreConexion.db
                .collection("usuarios")
                .document(uid)
                .collection("categorias")
                .document(nombreDocumento)

            batch.set(referencia, categoria)
        }

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al crear categorías")
            }
    }

    fun obtenerCategorias(
        uid: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .orderBy("orden")
            .get()
            .addOnSuccessListener { result ->
                val listaCategorias = result.documents.mapNotNull { doc ->
                    doc.getString("nombre")
                }
            Log.d("CATEGORIAS FIRESTORE", "usuario: $uid CATEGORIAS: $listaCategorias")
                onSuccess(listaCategorias)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al obtener categorías")
            }
    }

    fun obtenerCategoriasConProductos(
        uid: String,
        onSuccess: (List<String>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .orderBy("orden")
            .get()
            .addOnSuccessListener { categoriasResult ->

                val categorias = categoriasResult.documents

                if (categorias.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val categoriasConProductos = mutableListOf<String>()
                var pendientes = categorias.size
                var errorEnviado = false

                categorias.forEach { categoriaDoc ->
                    val nombre = categoriaDoc.getString("nombre") ?: ""
                    val categoriaId = categoriaDoc.id

                    FirestoreConexion.db
                        .collection("usuarios")
                        .document(uid)
                        .collection("categorias")
                        .document(categoriaId)
                        .collection("productos")
                        .limit(1)
                        .get()
                        .addOnSuccessListener { productosResult ->

                            if (!productosResult.isEmpty && nombre.isNotBlank()) {
                                categoriasConProductos.add(nombre)
                            }

                            pendientes--

                            if (pendientes == 0 && !errorEnviado) {
                                onSuccess(categoriasConProductos)
                            }
                        }
                        .addOnFailureListener { e ->
                            if (!errorEnviado) {
                                errorEnviado = true
                                onError(e.message ?: "Error al comprobar productos de categorías")
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al obtener categorías")
            }
    }

    /*fun contarCategoriasConProductos(
        uid: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        obtenerCategoriasConProductos(
            uid = uid,
            onSuccess = {
                onSuccess(it.size)
            },
            onError = onError
        )
    }*/
}