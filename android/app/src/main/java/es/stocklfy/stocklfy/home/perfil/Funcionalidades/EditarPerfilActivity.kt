package es.stocklfy.stocklfy.home.perfil.Funcionalidades

import android.net.Uri
import coil.compose.AsyncImage
import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.UsuarioFirestore
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class EditarPerfilActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val emailActual = user?.email ?: ""
        val nombreActual = emailActual.substringBefore("@")
        setContent {
            StocklfyTheme {
                EditarPerfilScreen(
                    emailActual = emailActual,
                    nombreInicial = "",
                    onCambiarFotoClick = {
                        Toast.makeText(
                            this,
                            "Aquí podrás abrir la galería más adelante",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onGuardarClick = { nuevoNombre ->
                        val uid = FirebaseAuth.getInstance().currentUser?.uid

                        if (uid != null) {
                            UsuarioFirestore().actualizarNombre(
                                uid,
                                nuevoNombre,
                                onSuccess = {
                                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT)
                                        .show()
                                    setResult(RESULT_OK)
                                    finish()
                                },
                                onError = { error ->
                                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun guardarCambios(nuevoNombre: String) {
        if (nuevoNombre.isBlank()) {
            Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(
            this,
            "Nombre actualizado: $nuevoNombre",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }
}

@Composable
fun EditarPerfilScreen(
    emailActual: String,
    nombreInicial: String,
    onCambiarFotoClick: (Uri) -> Unit,
    onGuardarClick: (String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            onCambiarFotoClick(uri)
        }
    }
    var nombre by remember { mutableStateOf(nombreInicial) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Editar perfil",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(110.dp)
                .background(Color.LightGray, CircleShape)
                .clickable {
                    launcherGaleria.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else
            {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = {
            launcherGaleria.launch("image/*")
        })
        {
            Text("Cambiar foto")
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = emailActual,
            onValueChange = {},
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onGuardarClick(nombre) },
            enabled = nombre.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Guardar cambios")
        }
        Button(
            onClick = { onBackClick() },
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Volver")
        }
    }
}