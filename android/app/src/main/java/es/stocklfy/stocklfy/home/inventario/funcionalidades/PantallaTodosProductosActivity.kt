package es.stocklfy.stocklfy.home.inventario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.ProductosFirestore
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import androidx.compose.material3.AssistChip
import androidx.compose.ui.unit.sp
import es.stocklfy.stocklfy.ui.components.BotonSecundario

class PantallaTodosProductosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val categoriaSeleccionada = intent.getStringExtra("categoria")
        setContent {
            StocklfyTheme {
                PantallaTodosProductos(
                    categoriaSeleccionada = categoriaSeleccionada,
                    onBackClick = { finish() }
                )
            }
        }
    }
}

data class Producto(
    val id: String,
    val nombre: String,
    val cantidad: String,
    val categoria: String,
    val fechaCaducidad: String,
    val notas: String
)

fun parseFechaProducto(fechaTexto: String): LocalDate? {

    val limpio = fechaTexto.trim()

    val formatos = listOf(
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d-M-yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy")
    )

    formatos.forEach { formatter ->
        try {
            return LocalDate.parse(limpio, formatter)
        } catch (_: Exception) {
            // ignoramos y probamos siguiente formato
        }
    }

    return null
}

fun obtenerColorProducto(producto: Producto): Color {

    val cantidad = producto.cantidad.toIntOrNull() ?: 0

    if (cantidad <= 0) {
        return Color(0xFFBDBDBD)
    }

    val fecha = parseFechaProducto(producto.fechaCaducidad) ?: return Color(0xFFBDBDBD)
    val hoy = LocalDate.now()
    val diasRestantes = ChronoUnit.DAYS.between(hoy, fecha)

    return when {
        diasRestantes < 0 -> Color(0xFFEF5350)
        diasRestantes <= 7 -> Color(0xFFFFC107)
        else -> Color(0xFF66BB6A)
    }
}

fun obtenerTextoCaducidad(fechaCaducidad: String): String {
    val fecha =
        parseFechaProducto(fechaCaducidad)
            ?: return "Fecha no válida"
    val hoy = LocalDate.now()
    val diasRestantes =
        ChronoUnit.DAYS.between(hoy, fecha)
    return when {
        diasRestantes < 0 -> {
            "Caducado hace ${-diasRestantes} días"
        }
        diasRestantes == 0L -> {
            "Caduca hoy"
        }
        diasRestantes == 1L -> {
            "Caduca mañana"
        }
        diasRestantes <= 7 -> {
            "Caduca en $diasRestantes días"
        }
        else -> {
            "Consumible"
        }
    }
}

@Composable
fun PantallaTodosProductos(
    categoriaSeleccionada: String?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val firestore = ProductosFirestore()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var filtro by remember { mutableStateOf("") }
    var ordenSeleccionado by remember { mutableStateOf("Nombre") }
    var expandedOrden by remember { mutableStateOf(false) }
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }

    fun convertirProductos(lista: List<Map<String, Any>>): List<Producto> {
        return lista.map { producto ->
            Producto(
                id = producto["id"] as? String ?: "",
                nombre = producto["nombre"] as? String ?: "",
                cantidad = producto["cantidad"] as? String ?: "",
                categoria = producto["categoria"] as? String ?: "",
                fechaCaducidad = producto["fechaCaducidad"] as? String ?: "",
                notas = producto["notas"] as? String ?: ""
            )
        }
    }

    fun cargarProductos() {
        if (uid == null) {
            Toast.makeText(context, "Usuario no logueado", Toast.LENGTH_SHORT).show()
            return
        }
        if (categoriaSeleccionada != null) {
            firestore.obtenerProductosPorCategoria(
                uid = uid,
                categoria = categoriaSeleccionada,
                onSuccess = { lista ->
                    productos = convertirProductos(lista)
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            firestore.obtenerProductos(
                uid = uid,
                onSuccess = { lista ->
                    productos = convertirProductos(lista)
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    LaunchedEffect(categoriaSeleccionada) {
        cargarProductos()
    }
    val productosFiltrados = productos.filter {
        it.nombre.contains(filtro, true) ||
                it.categoria.contains(filtro, true)
    }
    val productosOrdenados = when (ordenSeleccionado) {
        "Nombre" -> productosFiltrados.sortedBy { it.nombre }
        "Categoría" -> productosFiltrados.sortedBy { it.categoria }
        "Fecha" -> productosFiltrados.sortedBy { parseFechaProducto(it.fechaCaducidad) }
        "Cantidad" -> productosFiltrados.sortedBy { it.cantidad.toIntOrNull() ?: 0 }
        else -> productosFiltrados
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .navigationBarsPadding()
    )
    {
        Text(
            text = if (categoriaSeleccionada == null)
                "Catálogo de productos"
            else
                "Productos de $categoriaSeleccionada",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filtro,
                onValueChange = { filtro = it },
                label = { Text("Filtrar") },
                trailingIcon = {
                    if (filtro.isNotBlank()) {
                        IconButton(
                            onClick = {
                                filtro = ""
                            }
                        ) {
                            Text("✕")
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    cargarProductos()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refrescar"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    expandedOrden = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ordenar por: $ordenSeleccionado")
            }

            DropdownMenu(
                expanded = expandedOrden,
                onDismissRequest = {
                    expandedOrden = false
                }
            ) {
                listOf("Nombre", "Categoría", "Fecha", "Cantidad").forEach { orden ->

                    DropdownMenuItem(
                        text = {
                            Text(orden)
                        },
                        onClick = {
                            ordenSeleccionado = orden
                            expandedOrden = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Productos encontrados: ${productosOrdenados.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))
        if (productosOrdenados.isEmpty()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay productos para mostrar",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(productosOrdenados) { producto ->
                    ProductoCard(
                        producto = producto,
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
                        },
                        onEditar = {

                            val intent = Intent(context, EditarProductoActivity::class.java)
                            intent.putExtra("id", producto.id)
                            intent.putExtra("nombre", producto.nombre)
                            intent.putExtra("cantidad", producto.cantidad)
                            intent.putExtra("categoria", producto.categoria)
                            intent.putExtra("fechaCaducidad", producto.fechaCaducidad)
                            intent.putExtra("notas", producto.notas)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onBackClick,
            modifier = Modifier
                .width(220.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onEliminar: () -> Unit,
    onEditar: () -> Unit
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // 🔥 CATEGORÍA ARRIBA DEL TODO (más grande)
            Text(
                text = producto.categoria,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // BARRA IZQUIERDA
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(120.dp)
                        .background(
                            obtenerColorProducto(producto),
                            RoundedCornerShape(50)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // ICONO
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // CONTENIDO
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    // NOMBRE
                    Text(
                        text = producto.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    // ESTADO
                    val estado = estadoCaducidad(producto.fechaCaducidad)

                    Text(
                        text = obtenerTextoCaducidad(producto.fechaCaducidad),
                        fontWeight = FontWeight.SemiBold,
                        color = when (estado) {
                            -1 -> Color(0xFFD32F2F) // rojo caducado
                            0 -> Color(0xFFF9A825)  // amarillo
                            else -> Color(0xFF2E7D32) // verde
                        }
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // CADUCIDAD + CANTIDAD
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Caducidad:")
                            Text(producto.fechaCaducidad)
                        }

                        Column {
                            Text("Cantidad:")
                            Text(producto.cantidad)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // NOTAS
                    if (producto.notas.isNotBlank()) {
                        Text(
                            text = "Notas: ${producto.notas}"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BOTONES
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        OutlinedButton(
                            onClick = onEditar,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Editar")
                        }

                        Button(
                            onClick = { mostrarDialogoEliminar = true },
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Seguro que quieres eliminar ${producto.nombre}?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        onEliminar()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoEliminar = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
fun estadoCaducidad(fechaCaducidad: String): Int {
    val fecha = parseFechaProducto(fechaCaducidad)
        ?: return 0
    val dias = ChronoUnit.DAYS.between(LocalDate.now(), fecha)
    return when {
        dias < 0 -> -1   // caducado
        dias <= 7 -> 0   // alerta
        else -> 1        // ok
    }
}