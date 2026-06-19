package es.stocklfy.stocklfy.home.inventario.funcionalidades

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.ProductosFirestore
import es.stocklfy.stocklfy.home.inventario.EditarProductoActivity
import es.stocklfy.stocklfy.home.inventario.Producto
import es.stocklfy.stocklfy.home.inventario.ProductoCard
import es.stocklfy.stocklfy.ui.components.BotonSecundario
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class PantallaBuscarProductoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                PantallaBuscarProducto(
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun PantallaBuscarProducto(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = ProductosFirestore()
    var productos by remember {
        mutableStateOf<List<Producto>>(emptyList())
    }
    var textoBusqueda by remember {
        mutableStateOf("")
    }
    fun cargarProductos() {
        if (uid == null) return
        firestore.obtenerProductos(
            uid = uid,

            onSuccess = { lista ->

                productos = lista.map {

                    Producto(
                        id = it["id"] as? String ?: "",
                        nombre = it["nombre"] as? String ?: "",
                        cantidad = it["cantidad"] as? String ?: "",
                        categoria = it["categoria"] as? String ?: "",
                        fechaCaducidad = it["fechaCaducidad"] as? String ?: "",
                        notas = it["notas"] as? String ?: ""
                    )
                }
            },

            onError = {

                Toast.makeText(
                    context,
                    it,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
    LaunchedEffect(Unit) {
        cargarProductos()
    }
    val productosFiltrados = productos.filter {
        it.nombre.contains(textoBusqueda, true) ||
                it.categoria.contains(textoBusqueda, true) ||
                it.notas.contains(textoBusqueda, true)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Buscar producto",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = textoBusqueda,

            onValueChange = {
                textoBusqueda = it
            },

            label = {
                Text("Buscar")
            },

            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null
                )
            },

            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productosFiltrados) { producto ->

                ProductoCard(

                    producto = producto,

                    onEditar = {

                        val intent = Intent(
                            context,
                            EditarProductoActivity::class.java
                        )

                        intent.putExtra("id", producto.id)
                        intent.putExtra("nombre", producto.nombre)
                        intent.putExtra("cantidad", producto.cantidad)
                        intent.putExtra("categoria", producto.categoria)
                        intent.putExtra("fechaCaducidad", producto.fechaCaducidad)
                        intent.putExtra("notas", producto.notas)

                        context.startActivity(intent)
                    },

                    onEliminar = {

                        if (uid != null) {

                            firestore.eliminarProducto(

                                uid = uid,
                                productoId = producto.id,
                                categoria = producto.categoria,

                                onSuccess = {

                                    Toast.makeText(
                                        context,
                                        "es.stocklfy.stocklfy.home.inventario.Producto eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    cargarProductos()
                                },

                                onError = {

                                    Toast.makeText(
                                        context,
                                        it,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                    }
                )
            }
        }
        BotonSecundario(
            texto = "Volver",
            onClick = onBackClick,
            modifier = Modifier
                .width(220.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}