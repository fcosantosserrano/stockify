package es.stocklfy.stocklfy.home.compras.funcionalidades

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.CategoriasFirestore
import es.stocklfy.stocklfy.firebase.firestore.opciones.ProductosFirestore
import es.stocklfy.stocklfy.global.capitalizarPrimeraEnTiempoReal
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import es.stocklfy.stocklfy.ui.components.BotonPrincipal
import es.stocklfy.stocklfy.ui.components.BotonSecundario

class AgregarProductoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {

                var mostrarDialogoCaducado by remember {
                    mutableStateOf(false)
                }
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

                LaunchedEffect(Unit) {
                    if (uid != null) {
                        CategoriasFirestore().obtenerCategorias(
                            uid = uid,
                            onSuccess = {
                                categorias = it
                            },
                            onError = { error ->
                                Toast.makeText(
                                    this@AgregarProductoActivity,
                                    error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }

                AgregarProductoScreen(
                    categorias = categorias,
                    onGuardarClick = { nombre, cantidad, categoria, fechaCaducidad, notas ->

                        if (uid != null) {

                            val productosFirestore = ProductosFirestore()

                            productosFirestore.obtenerProductos(
                                uid = uid,

                                onSuccess = { lista ->

                                    val productoDuplicado = lista.any { producto ->

                                        val nombreExistente =
                                            producto["nombre"] as? String ?: ""

                                        val categoriaExistente =
                                            producto["categoria"] as? String ?: ""

                                        val fechaExistente =
                                            producto["fechaCaducidad"] as? String ?: ""

                                        nombreExistente.equals(nombre, ignoreCase = true) &&
                                                categoriaExistente.equals(categoria, ignoreCase = true) &&
                                                fechaExistente == fechaCaducidad
                                    }

                                    if (productoDuplicado) {

                                        Toast.makeText(
                                            this,
                                            "Este producto ya existe con la misma fecha de caducidad",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    } else {

                                        productosFirestore.guardarProducto(
                                            uid = uid,
                                            nombre = nombre,
                                            cantidad = cantidad,
                                            categoria = categoria,
                                            fechaCaducidad = fechaCaducidad,
                                            notas = notas,

                                            onSuccess = {
                                                Toast.makeText(
                                                    this,
                                                    "Producto guardado",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                finish()
                                            },

                                            onError = { error ->
                                                Toast.makeText(
                                                    this,
                                                    error,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        )
                                    }
                                },

                                onError = { error ->
                                    Toast.makeText(
                                        this,
                                        error,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )

                        } else {
                            Toast.makeText(
                                this,
                                "Error: usuario no logueado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onVolverClick = {
                        finish()
                    }
                )
            }

        }
    }
}
fun formatearFecha(input: String): String {

    val numeros = input.filter { it.isDigit() }.take(8)

    val sb = StringBuilder()

    numeros.forEachIndexed { index, c ->

        if (index == 2 || index == 4) sb.append("/")

        sb.append(c)
    }

    return sb.toString()
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    categorias: List<String>,
    onGuardarClick: (
        nombre: String,
        cantidad: String,
        categoria: String,
        fechaCaducidad: String,
        notas: String
    ) -> Unit,
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf(TextFieldValue("")) }
    var cantidad by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fechaCaducidad by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    var mostrarDialogoCaducado by remember { mutableStateOf(false) }
    var fecha by remember { mutableStateOf(TextFieldValue("")) }
    var mostrarDialogoSinFecha by remember { mutableStateOf(false) }

    fun productoCaducado(fechaTexto: String): Boolean {
        return try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fechaProducto = java.time.LocalDate.parse(fechaTexto.replace("/", "-"), formatter)
            fechaProducto.isBefore(java.time.LocalDate.now())
        } catch (_: Exception) {
            false
        }
    }

    fun guardarProducto() {
        if (nombre.text.isBlank() || cantidad.isBlank() || categoria.isBlank()) {
            Toast.makeText(
                context,
                "Completa los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val fechaNormalizada = fecha.text.trim().replace("-", "/")
        onGuardarClick(
            nombre.text,
            cantidad,
            categoria,
            fechaNormalizada,
            notas.text
        )    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "es.stocklfy.stocklfy.home.inventario.Producto",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Añadir producto",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nuevo ->
                nombre = capitalizarPrimeraEnTiempoReal(nuevo)
            },
            label = { Text("Nombre del producto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it.filter { char -> char.isDigit() }
            },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = categoria,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            categoria = opcion
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = { nuevo ->

                val soloNumeros = nuevo.text.filter { it.isDigit() }.take(8)

                val formateado = buildString {

                    soloNumeros.forEachIndexed { index, c ->
                        if (index == 2 || index == 4) append("/")
                        append(c)
                    }
                }

                fecha = TextFieldValue(
                    text = formateado,
                    selection = TextRange(formateado.length)
                )
            },
            label = { Text("Fecha de caducidad (dd/MM/yyyy)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = notas,
            onValueChange = { nuevo ->
                notas = capitalizarPrimeraEnTiempoReal(nuevo)
            },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(28.dp))

        BotonPrincipal(
            texto = "Guardar producto",
            onClick = {
                if (
                    nombre.text.isBlank() ||
                    cantidad.isBlank() ||
                    categoria.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Completa los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@BotonPrincipal
                }

// NUEVO CONTROL FECHA VACÍA
                if (fecha.text.isBlank()) {
                    mostrarDialogoSinFecha = true
                    return@BotonPrincipal
                }

                val fechaNormalizada = fecha.text.replace("/", "-")

                if (productoCaducado(fechaNormalizada)) {
                    mostrarDialogoCaducado = true
                } else {
                    guardarProducto()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onVolverClick
        )
    }

    if (mostrarDialogoCaducado) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoCaducado = false
            },
            title = {
                Text("Producto caducado")
            },
            text = {
                Text("La fecha introducida es anterior a la fecha actual. ¿Quieres guardar el producto igualmente?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCaducado = false
                        guardarProducto()
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        mostrarDialogoCaducado = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mostrarDialogoSinFecha) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoSinFecha = false
            },
            title = {
                Text("Fecha no introducida")
            },
            text = {
                Text("No has introducido fecha de caducidad. ¿Deseas continuar sin ella?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoSinFecha = false

                        // guardas sin fecha
                        val fechaNormalizada = ""

                        onGuardarClick(
                            nombre.text,
                            cantidad,
                            categoria,
                            fechaNormalizada,
                            notas.text
                        )
                    }
                ) {
                    Text("Continuar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        mostrarDialogoSinFecha = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}