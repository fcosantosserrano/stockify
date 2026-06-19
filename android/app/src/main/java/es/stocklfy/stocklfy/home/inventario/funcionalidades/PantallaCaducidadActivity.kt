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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class PantallaCaducidadActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                PantallaCaducidad(
                    onBackClick = { finish() },
                )
            }
        }
    }
}

@Composable
fun PantallaCaducidad(
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = ProductosFirestore()
    var productos by remember {
        mutableStateOf<List<Producto>>(emptyList())
    }
    var diasSeleccionados by remember {
        mutableStateOf(7)
    }
    var expanded by remember {
        mutableStateOf(false)
    }
    fun parseFecha(fechaTexto: String): LocalDate? {
        val formatos = listOf(
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        formatos.forEach { formatter ->
            try {
                return LocalDate.parse(fechaTexto, formatter)
            } catch (_: Exception) {
            }
        }
        return null
    }

    fun cargarProductos() {
        if (uid == null) return
        firestore.obtenerProductos(
            uid = uid,
            onSuccess = { lista ->
                val hoy = LocalDate.now()
                val productosFiltrados = lista.mapNotNull { producto ->
                    val fechaTexto =
                        producto["fechaCaducidad"] as? String
                            ?: return@mapNotNull null
                    val fecha =
                        parseFecha(fechaTexto)
                            ?: return@mapNotNull null
                    val diasRestantes =
                        ChronoUnit.DAYS.between(hoy, fecha)
                    if (diasRestantes in 0..diasSeleccionados.toLong()) {
                        Producto(
                            id = producto["id"] as? String ?: "",
                            nombre = producto["nombre"] as? String ?: "",
                            cantidad = producto["cantidad"] as? String ?: "",
                            categoria = producto["categoria"] as? String ?: "",
                            fechaCaducidad = fechaTexto,
                            notas = producto["notas"] as? String ?: ""
                        )
                    } else {
                        null
                    }
                }.sortedBy {
                    parseFecha(it.fechaCaducidad)
                }
                productos = productosFiltrados
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

    LaunchedEffect(diasSeleccionados) {
        cargarProductos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Próximos a caducar",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = {
                    expanded = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    "Mostrar: $diasSeleccionados días"
                )
            }

            DropdownMenu(
                expanded = expanded,

                onDismissRequest = {
                    expanded = false
                }

            ) {

                listOf(7, 14, 28).forEach { dias ->

                    DropdownMenuItem(

                        text = {
                            Text("$dias días")
                        },

                        onClick = {
                            diasSeleccionados = dias
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(productos) { producto ->

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
                                        "Producto eliminado",
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