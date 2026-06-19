package es.stocklfy.stocklfy.home.compras.funcionalidades

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import es.stocklfy.stocklfy.BuildConfig
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import es.stocklfy.stocklfy.ui.components.BotonPrincipal
import es.stocklfy.stocklfy.ui.components.BotonSecundario

class LectorTicketsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                LectorTicketsScreen(
                    onProcesarImagen = { uri, onResultado ->
                        procesarImagen(uri, onResultado)
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun procesarImagen(uri: Uri, onResultado: (String) -> Unit) {
        try {
            val image = InputImage.fromFilePath(this, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val textoLimpio = limpiarTextoOCR(visionText.text)
                    interpretarConGemini(textoLimpio) { resultado ->
                        onResultado(resultado)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, e.message ?: "Error al leer ticket", Toast.LENGTH_LONG)
                        .show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, e.message ?: "Error al cargar imagen", Toast.LENGTH_LONG).show()
        }
    }

    private fun interpretarConGemini(textoOCR: String, onResultado: (String) -> Unit) {
        val model = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )

        val prompt = """
            Extrae los productos y precios de este ticket de supermercado.
            Devuelve SOLO un JSON con este formato exacto, sin texto extra ni backticks:
            {
                "productos": [
                    {"nombre": "NOMBRE PRODUCTO", "precio": "0,00"}
                ],
                "total": "0,00"
            }
            
            Texto del ticket:
            $textoOCR
        """.trimIndent()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = model.generateContent(prompt)
                val json = response.text ?: ""
                withContext(Dispatchers.Main) {
                    onResultado(parsearRespuestaGemini(json))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onResultado("Error al interpretar con Gemini: ${e.message}")
                }
            }
        }
    }

    private fun parsearRespuestaGemini(json: String): String {
        return try {
            val jsonLimpio = json
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val obj = org.json.JSONObject(jsonLimpio)
            val productos = obj.getJSONArray("productos")
            val total = obj.getString("total")

            buildString {
                append("🛒 PRODUCTOS:\n\n")
                for (i in 0 until productos.length()) {
                    val producto = productos.getJSONObject(i)
                    append("• ${producto.getString("nombre")} → ${producto.getString("precio")}\n")
                }
                append("\n💰 TOTAL: $total")
            }
        } catch (e: Exception) {
            "Error al parsear respuesta: ${e.message}"
        }
    }

    private fun limpiarTextoOCR(texto: String): String {
        return texto
            .replace("T0TAL", "TOTAL")
            .replace("t0tal", "total")
            .replace("b,", "6,")
            .replace("l0", "10")
            .replace("OP:l", "OP:1")
            .replace(Regex("""(\d+),\s+(\d{2})"""), "$1,$2")
    }
}

@Composable
fun LectorTicketsScreen(
    onProcesarImagen: (Uri, (String) -> Unit) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var textoDetectado by remember { mutableStateOf("") }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            onProcesarImagen(uri) { texto ->
                textoDetectado = texto
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

        Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Lector de tickets",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona una foto de un ticket para extraer el texto automáticamente.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        BotonPrincipal(
            texto = "Seleccionar imagen",
            onClick = {
                launcherGaleria.launch("image/*")
            },
            modifier = Modifier.width(240.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Texto detectado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Text(
                text =
                    if (textoDetectado.isBlank())
                        "Aquí aparecerá el texto del ticket..."
                    else
                        textoDetectado,

                modifier = Modifier.padding(18.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onBackClick,
            modifier = Modifier.width(180.dp)
        )
    }
}