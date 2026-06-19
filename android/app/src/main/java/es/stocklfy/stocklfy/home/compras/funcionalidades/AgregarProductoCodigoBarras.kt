package es.stocklfy.stocklfy.home.compras.funcionalidades

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
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
import es.stocklfy.stocklfy.ui.components.BotonPrincipal
import es.stocklfy.stocklfy.ui.components.BotonSecundario
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class AgregarProductoCodigoBarras : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                var categorias by remember { mutableStateOf<List<String>>(emptyList()) }

                LaunchedEffect(Unit) {
                    if (uid != null) {
                        CategoriasFirestore().obtenerCategorias(
                            uid = uid,
                            onSuccess = { categorias = it },
                            onError = { error ->
                                Toast.makeText(
                                    this@AgregarProductoCodigoBarras,
                                    error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }

                AgregarProductoCodigoBarrasScreen(
                    uid = uid,
                    categorias = categorias,
                    onGuardarClick = { nombre, cantidad, categoriaFinal, fechaCaducidad, notas, codigoBarras ->

                        if (uid != null) {
                            ProductosFirestore().guardarProducto(
                                uid = uid,
                                nombre = nombre,
                                cantidad = cantidad,
                                categoria = categoriaFinal,
                                fechaCaducidad = fechaCaducidad.replace("/", "-"),
                                notas = notas,
                                codigoBarras = codigoBarras,
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

//fun procesarImagenFecha(bitmap: Bitmap, onFechaDetectada: (String) -> Unit) {
//
//    val image = InputImage.fromBitmap(bitmap, 0)
//
//    val recognizer = TextRecognition.getClient(
//        TextRecognizerOptions.DEFAULT_OPTIONS
//    )
//
//    recognizer.process(image)
//        .addOnSuccessListener { result ->
//
//            val texto = result.text
//            Log.d("OCR", "Bitmap: ${bitmap.width} x ${bitmap.height}")
//            Log.d("OCR", "Texto detectado: $texto")
//            Log.d("OCR", "TEXTO RAW => '$texto'")
//
//            // aquí buscas la fecha
//            val regex = Regex("""\b\d{2}[/-]\d{2}[/-]\d{4}\b""")
//            val match = regex.find(texto)
//
//            if (match != null) {
//                onFechaDetectada(match.value)
//            }
//
//        }
//        .addOnFailureListener { error ->
//            Log.e("OCR", "Error: ${error.message}")
//        }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoCodigoBarrasScreen(
    uid: String?,
    categorias: List<String>,
    onGuardarClick: (
        nombre: String,
        cantidad: String,
        categoria: String,
        fechaCaducidad: String,
        notas: String,
        codigoBarras: String
    ) -> Unit,
    onVolverClick: () -> Unit
) {
    val context = LocalContext.current

    var nombre: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var cantidad by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fechaCaducidad by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf(TextFieldValue("")) }
    var codigoBarras by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(TextFieldValue("")) }
    var tipoProducto by remember { mutableStateOf("") }
    var tipoRefresco by remember { mutableStateOf("") }
    var mostrarDialogoSinFecha by remember { mutableStateOf(false) }
    var notasFinales by remember { mutableStateOf("") }

//    val launcherFecha = rememberLauncherForActivityResult(
//        ActivityResultContracts.TakePicturePreview()
//    ) { bitmap ->
//        if (bitmap != null) {
//            procesarImagenFecha(bitmap) { fecha ->
//                fechaCaducidad = fecha
//            }
//        } else {
//            Toast.makeText(context, "No se hizo la foto", Toast.LENGTH_SHORT).show()
//        }
//    }

fun formatearFecha(input: String): String {

    val numeros = input.filter { it.isDigit() }.take(8)

    val sb = StringBuilder()

    numeros.forEachIndexed { index, c ->

        if (index == 2 || index == 4) sb.append("/")

        sb.append(c)
    }

    return sb.toString()
}


    var expandedCategoria by remember { mutableStateOf(false) }

    fun buscarProductoExistente(codigo: String) {
        if (uid == null || codigo.isBlank()) return

        ProductosFirestore().buscarProductoPorCodigo(
            uid = uid,
            codigoBarras = codigo,
            onSuccess = { producto ->

                if (producto != null) {
                    nombre = TextFieldValue(producto["nombre"] as? String ?: "")
                    cantidad = producto["cantidad"] as? String ?: ""
                    fechaCaducidad = producto["fechaCaducidad"] as? String ?: ""
                    notas = TextFieldValue(producto["notas"] as? String ?: "")

                    val categoriaGuardada =
                        producto["categoria"] as? String ?: ""

                    if (categoriaGuardada.startsWith("Bebida")) {
                        tipoProducto = "Bebida"
                        tipoRefresco = categoriaGuardada
                            .replace("Bebida -", "")
                            .trim()
                    } else {
                        tipoProducto = "Comida"
                        categoria = categoriaGuardada
                    }

                    Toast.makeText(
                        context,
                        "Producto encontrado y autocompletado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onError = { error ->
                Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    val launcherEscaner = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val codigo =
                result.data?.getStringExtra("codigoBarras") ?: ""

            if (codigo.isNotBlank()) {
                codigoBarras = codigo
                buscarProductoExistente(codigo)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                modifier = Modifier.size(50.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Añadir producto con código",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        BotonPrincipal(
            texto = "Abrir cámara",
            onClick = {
                val intent = Intent(
                    context,
                    EscanerCodigoBarrasActivity::class.java
                )

                launcherEscaner.launch(intent)
            },
            modifier = Modifier.width(220.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigoBarras,
            onValueChange = {
                codigoBarras = it
            },
            label = {
                Text("Código de barras")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Tipo de producto",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = tipoProducto == "Comida",
                onClick = {
                    tipoProducto = "Comida"
                    tipoRefresco = ""
                },
                label = {
                    Text("Comida")
                }
            )

            FilterChip(
                selected = tipoProducto == "Bebida",
                onClick = {
                    tipoProducto = "Bebida"
                    categoria = ""
                },
                label = {
                    Text("Bebida")
                }
            )

            FilterChip(
                selected = tipoProducto == "Otro",
                onClick = {
                    tipoProducto = "Otro"
                    categoria = ""
                    tipoRefresco = ""
                },
                label = {
                    Text("Otro")
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nuevo ->
                nombre = capitalizarPrimeraEnTiempoReal(nuevo)
            },
            label = {
                Text("Nombre del producto")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (tipoProducto == "Comida") {

            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = {
                    expandedCategoria = !expandedCategoria
                }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Categoría")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoria)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = {
                        expandedCategoria = false
                    }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = {
                                Text(opcion)
                            },
                            onClick = {
                                categoria = opcion
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (tipoProducto == "Bebida") {

            OutlinedTextField(
                value = tipoRefresco,
                onValueChange = { nuevo ->
                    tipoRefresco = nuevo.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                },
                label = {
                    Text("Tipo de bebida")
                },
                placeholder = {
                    Text("Cola, naranja, limón...")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        OutlinedTextField(
            value = cantidad,
            onValueChange = {
                cantidad = it.filter { char -> char.isDigit() }
            },
            label = {
                Text("Cantidad")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

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

//        BotonPrincipal(
//            texto = "Escanear fecha",
//            onClick = {
//                launcherFecha.launch(null)
//            },
//            modifier = Modifier.width(220.dp)
//        )

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

        Spacer(modifier = Modifier.height(24.dp))

        BotonPrincipal(
            texto = "Guardar producto",
            onClick = {

                val categoriaFinal =
                    when (tipoProducto) {
                        "Comida" -> categoria
                        "Bebida" -> "Bebidas"
                        "Otro" -> "Otros"
                        else -> ""
                    }

                if (
                    codigoBarras.isBlank() ||
                    nombre.text.isBlank() ||
                    cantidad.isBlank() ||
                    tipoProducto.isBlank() ||
                    categoriaFinal.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Completa los campos obligatorios",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@BotonPrincipal
                }

                if (fecha.text.isBlank()) {
                    mostrarDialogoSinFecha = true
                    return@BotonPrincipal
                }

                val notasFinales =
                    if (tipoProducto == "Bebida" && tipoRefresco.isNotBlank()) {
                        "Tipo de bebida: $tipoRefresco. ${notas.text}"
                    } else {
                        notas.text
                    }

                onGuardarClick(
                    nombre.text,
                    cantidad,
                    categoriaFinal,
                    fecha.text.trim(),
                    notasFinales,
                    codigoBarras
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onVolverClick,
            modifier = Modifier.width(180.dp)
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

                        val categoriaFinal =
                            when (tipoProducto) {
                                "Comida" -> categoria
                                "Bebida" -> "Bebidas"
                                "Otro" -> "Otros"
                                else -> ""
                            }

                        val notasFinales =
                            if (tipoProducto == "Bebida" && tipoRefresco.isNotBlank()) {
                                "Tipo de bebida: $tipoRefresco. ${notas.text}"
                            } else {
                                notas.text
                            }

                        onGuardarClick(
                            nombre.text,
                            cantidad,
                            categoriaFinal,
                            fecha.text.trim(),
                            notasFinales,
                            codigoBarras
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
