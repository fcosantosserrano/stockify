package es.stocklfy.stocklfy.home.perfil.Funcionalidades

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class CambiarPasswordActivity : ComponentActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                CambiarPasswordScreen(
                    onCambiarClick = { currentPassword, newPassword, repeatPassword ->
                        cambiarPassword(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            repeatPassword = repeatPassword
                        )
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun cambiarPassword(
        currentPassword: String,
        newPassword: String,
        repeatPassword: String
    ) {
        if (currentPassword.isBlank() || newPassword.isBlank() || repeatPassword.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword != repeatPassword) {
            Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPassword.length < 6) {
            Toast.makeText(
                this,
                "La nueva contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email.isNullOrBlank()) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
            return
        }
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Contraseña actualizada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    updateTask.exception?.message
                                        ?: "Error al actualizar la contraseña",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "La contraseña actual no es correcta",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}

@Composable
fun CambiarPasswordScreen(
    onCambiarClick: (String, String, String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var actualPassword by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var repetirPassword by remember { mutableStateOf("") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showRepeat by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    )
    {
        Text(
            text = "Cambiar contraseña",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        PasswordField(
            value = actualPassword,
            onValueChange = { actualPassword = it },
            label = "Escribe la contraseña actual",
            visible = showCurrent,
            onToggle = { showCurrent = !showCurrent }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(
            value = nuevaPassword,
            onValueChange = { nuevaPassword = it },
            label = "Escriba la contraseña",
            visible = showNew,
            onToggle = { showNew = !showNew }
        )
        Spacer(modifier = Modifier.height(16.dp))
        PasswordField(
            value = repetirPassword,
            onValueChange = { repetirPassword = it },
            label = "Escriba de nuevo la contraseña",
            visible = showRepeat,
            onToggle = { showRepeat = !showRepeat }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
                onCambiarClick(actualPassword, nuevaPassword, repetirPassword)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Cambiar contraseña")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Requisitos de contraseña:",
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Debe contener al menos 6 carácteres.")
        Text("Debe contener al menos 1 dígito.")
        Text("Debe contener al menos 1 carácter en minúscula.")
        Text("Debe contener al menos 1 letra en mayúscula.")
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text("Volver")
        }
    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        }
    )
}