package es.stocklfy.stocklfy.home.compras.funcionalidades

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import java.util.concurrent.Executors

class EscanerCodigoBarrasActivity : ComponentActivity() {

    private var codigoDetectado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }

        setContent {
            StocklfyTheme {
                PantallaEscanerCodigoBarras(
                    onCodigoDetectado = { codigo ->

                        if (!codigoDetectado) {
                            codigoDetectado = true

                            val intent = Intent()
                            intent.putExtra("codigoBarras", codigo)

                            setResult(Activity.RESULT_OK, intent)
                            finish()
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

@Composable
fun PantallaEscanerCodigoBarras(
    onCodigoDetectado: (String) -> Unit,
    onVolverClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreviewCodigoBarras(
            onCodigoDetectado = onCodigoDetectado
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(180.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(
                    width = 3.dp,
                    color = Color.White
                )
            ) {}
        }

        Text(
            text = "Enfoca el código de barras dentro del rectángulo",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )

        Button(
            onClick = onVolverClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("Volver")
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewCodigoBarras(
    onCodigoDetectado: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            val previewView = PreviewView(ctx)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider =
                    cameraProviderFuture.get()

                val preview =
                    Preview.Builder().build()

                preview.setSurfaceProvider(
                    previewView.surfaceProvider
                )

                val scanner =
                    BarcodeScanning.getClient()

                val executor =
                    Executors.newSingleThreadExecutor()

                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )
                        .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->

                    val mediaImage = imageProxy.image

                    if (mediaImage != null) {

                        val image =
                            InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )

                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->

                                val codigo = barcodes.firstOrNull()?.rawValue

                                if (!codigo.isNullOrBlank()) {
                                    onCodigoDetectado(codigo)
                                }
                            }
                            .addOnFailureListener {
                                imageProxy.close()
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }

                    } else {
                        imageProxy.close()
                    }
                }

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )

                } catch (_: Exception) {
                }

            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}