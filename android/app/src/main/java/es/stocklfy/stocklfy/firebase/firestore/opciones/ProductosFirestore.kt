package es.stocklfy.stocklfy.firebase.firestore.opciones

import es.stocklfy.stocklfy.global.normalizarCategoria
import es.stocklfy.stocklfy.firebase.firestore.FirestoreConexion
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProductosFirestore {

    fun guardarProducto(
        uid: String,
        nombre: String,
        cantidad: String,
        categoria: String,
        fechaCaducidad: String,
        notas: String,
        codigoBarras: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val db = FirestoreConexion.db

        val categoriaId = normalizarCategoria(categoria)

        val productoRef = db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .document()

        val productoId = productoRef.id

        val producto = hashMapOf(
            "id" to productoId,
            "nombre" to nombre,
            "cantidad" to cantidad,
            "categoria" to categoria,
            "categoriaId" to categoriaId,
            "fechaCaducidad" to fechaCaducidad,
            "notas" to notas,
            "codigoBarras" to codigoBarras
        )

        val productoCategoriaRef = db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .document(categoriaId)
            .collection("productos")
            .document(productoId)

        val batch = db.batch()

        batch.set(productoRef, producto)
        batch.set(productoCategoriaRef, producto)

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al guardar producto")
            }
    }

    fun contarProductos(
        uid: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .get()
            .addOnSuccessListener { result ->
                onSuccess(result.size())
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al contar productos")
            }
    }

    fun obtenerProductos(
        uid: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .get()
            .addOnSuccessListener { resultado ->
                val productos = resultado.documents.mapNotNull { doc ->
                    doc.data
                }

                onSuccess(productos)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al obtener productos")
            }
    }

    fun obtenerProductosPorCategoria(
        uid: String,
        categoria: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onError: (String) -> Unit
    ) {
        val categoriaId = normalizarCategoria(categoria)

        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .document(categoriaId)
            .collection("productos")
            .get()
            .addOnSuccessListener { resultado ->
                val productos = resultado.documents.mapNotNull { doc ->
                    doc.data
                }

                onSuccess(productos)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al obtener productos por categoría")
            }
    }
    fun contarProductosCaducan(
        uid: String,
        diasLimite: Long = 7,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        obtenerProductos(
            uid = uid,
            onSuccess = { lista ->

                val hoy = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                val cantidad = lista.count { producto ->

                    try {

                        val fechaTexto =
                            producto["fechaCaducidad"] as? String ?: return@count false

                        val fecha = LocalDate.parse(fechaTexto, formatter)

                        !fecha.isBefore(hoy) &&
                                fecha.isBefore(hoy.plusDays(diasLimite + 1))

                    } catch (_: Exception) {
                        false
                    }
                }

                onSuccess(cantidad)
            },
            onError = onError
        )
    }

    fun eliminarProducto(
        uid: String,
        productoId: String,
        categoria: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val db = FirestoreConexion.db
        val categoriaId = normalizarCategoria(categoria)

        val productoGeneralRef = db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .document(productoId)

        val productoCategoriaRef = db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .document(categoriaId)
            .collection("productos")
            .document(productoId)

        val batch = db.batch()

        batch.delete(productoGeneralRef)
        batch.delete(productoCategoriaRef)

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al eliminar producto")
            }
    }

    fun contarProductosCaducados(
        uid: String,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        obtenerProductos(
            uid = uid,
            onSuccess = { lista ->

                val hoy = LocalDate.now()
                val formatos = listOf(
                    DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
                fun parseFecha(fechaTexto: String): LocalDate? {
                    formatos.forEach { formatter ->
                        try {
                            return LocalDate.parse(fechaTexto.trim(), formatter)
                        } catch (_: Exception) {}
                    }
                    return null
                }
                val caducados = lista.count { producto ->
                    val fechaTexto = producto["fechaCaducidad"] as? String ?: return@count false
                    val fecha = parseFecha(fechaTexto) ?: return@count false

                    fecha.isBefore(hoy)
                }

                onSuccess(caducados)
            },
            onError = onError
        )
    }

    fun actualizarProducto(
        uid: String,
        productoId: String,
        nombre: String,
        cantidad: String,
        categoria: String,
        fechaCaducidad: String,
        notas: String,
        codigoBarras: String = "",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val db = FirestoreConexion.db
        val categoriaId = normalizarCategoria(categoria)

        val producto = hashMapOf(
            "id" to productoId,
            "nombre" to nombre,
            "cantidad" to cantidad,
            "categoria" to categoria,
            "categoriaId" to categoriaId,
            "fechaCaducidad" to fechaCaducidad.replace("/", "-"),
            "notas" to notas,
            "codigoBarras" to codigoBarras
        )

        val productoGeneralRef = db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .document(productoId)

        val productoCategoriaRef = db
            .collection("usuarios")
            .document(uid)
            .collection("categorias")
            .document(categoriaId)
            .collection("productos")
            .document(productoId)

        val batch = db.batch()

        batch.set(productoGeneralRef, producto)
        batch.set(productoCategoriaRef, producto)

        batch.commit()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error al actualizar producto")
            }
    }

    fun buscarProductoPorCodigo(
        uid: String,
        codigoBarras: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onError: (String) -> Unit
    ) {
        FirestoreConexion.db
            .collection("usuarios")
            .document(uid)
            .collection("productos")
            .whereEqualTo("codigoBarras", codigoBarras)
            .limit(1)
            .get()
            .addOnSuccessListener { resultado ->
                onSuccess(resultado.documents.firstOrNull()?.data)
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Error buscando producto")
            }
    }


}