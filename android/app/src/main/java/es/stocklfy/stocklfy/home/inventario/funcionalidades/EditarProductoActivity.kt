package es.stocklfy.stocklfy.home.inventario

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.ProductosFirestore
import es.stocklfy.stocklfy.global.capitalizarPrimera
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import androidx.compose.ui.text.input.TextFieldValue

class EditarProductoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val id = intent.getStringExtra("id") ?: ""
        val nombreInicial = intent.getStringExtra("nombre") ?: ""
        val cantidadInicial = intent.getStringExtra("cantidad") ?: ""
        val categoriaInicial = intent.getStringExtra("categoria") ?: ""
        val fechaInicial = intent.getStringExtra("fechaCaducidad") ?: ""
        val notasInicial = intent.getStringExtra("notas") ?: ""

        setContent {
            StocklfyTheme {
                EditarProductoScreen(
                    id = id,
                    nombreInicial = nombreInicial,
                    cantidadInicial = cantidadInicial,
                    categoriaInicial = categoriaInicial,
                    fechaInicial = fechaInicial,
                    notasInicial = notasInicial,
                    onBackClick = { finish() },
                    onProductoActualizado = { finish() }
                )
            }
        }
    }
}
fun formatearFechaInicial(fecha: String): String {

    val soloNumeros = fecha.filter { it.isDigit() }.take(8)

    return buildString {
        soloNumeros.forEachIndexed { index, c ->
            if (index == 2 || index == 4) append("/")
            append(c)
        }
    }
}

@Composable
fun EditarProductoScreen(
    id: String,
    nombreInicial: String,
    cantidadInicial: String,
    categoriaInicial: String,
    fechaInicial: String,
    notasInicial: String,
    onBackClick: () -> Unit,
    onProductoActualizado: () -> Unit
) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var nombre by remember { mutableStateOf(nombreInicial) }
    var cantidad by remember { mutableStateOf(cantidadInicial) }
    var categoria by remember { mutableStateOf(categoriaInicial) }
    var fechaCaducidad by remember {
        mutableStateOf(
            TextFieldValue(
                text = formatearFechaInicial(fechaInicial),
                selection = TextRange(formatearFechaInicial(fechaInicial).length)
            )
        )
    }
    var notas by remember { mutableStateOf(notasInicial) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        Text(
            text = "Editar producto",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ===================== NOMBRE =====================
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = capitalizarPrimera(it)
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===================== CANTIDAD =====================
        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===================== CATEGORÍA =====================
        OutlinedTextField(
            value = categoria,
            onValueChange = {
                categoria = capitalizarPrimera(it)
            },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===================== FECHA =====================
        /*OutlinedTextField(            value = fechaCaducidad,
            onValueChange = { fechaCaducidad = it },
            label = { Text("Fecha caducidad") },
            modifier = Modifier.fillMaxWidth()
        )*/

        OutlinedTextField(
            value = fechaCaducidad,
            onValueChange = { nuevo ->

                val soloNumeros = nuevo.text.filter { it.isDigit() }.take(8)

                val formateado = buildString {
                    soloNumeros.forEachIndexed { index, c ->
                        if (index == 2 || index == 4) append("/")
                        append(c)
                    }
                }

                fechaCaducidad = TextFieldValue(
                    text = formateado,
                    selection = TextRange(formateado.length)
                )
            },
            label = { Text("Fecha caducidad (dd/MM/yyyy)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===================== NOTAS =====================
        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===================== BOTÓN GUARDAR =====================
        Button(
            onClick = {
                if (uid != null) {
                    ProductosFirestore().actualizarProducto(
                        uid = uid,
                        productoId = id,
                        nombre = nombre,
                        cantidad = cantidad,
                        categoria = categoria,
                        fechaCaducidad = fechaCaducidad.text.trim(),
                        notas = notas,
                        onSuccess = {
                            Toast.makeText(
                                context,
                                "Producto actualizado",
                                Toast.LENGTH_SHORT
                            ).show()

                            onProductoActualizado()
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}