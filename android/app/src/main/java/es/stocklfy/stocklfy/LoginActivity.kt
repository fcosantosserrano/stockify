package es.stocklfy.stocklfy

import android.content.Intent
import androidx.compose.runtime.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material3.TextFieldDefaults
import es.stocklfy.stocklfy.firebase.AuthFirebase
import es.stocklfy.stocklfy.firebase.firestore.opciones.CategoriasFirestore
import es.stocklfy.stocklfy.firebase.firestore.opciones.UsuarioFirestore
import es.stocklfy.stocklfy.home.HomeActivity
import es.stocklfy.stocklfy.ui.theme.blanco
import es.stocklfy.stocklfy.ui.theme.verdeClaro
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.text.style.TextAlign

class LoginActivity : ComponentActivity() {

    private val authFirebase = AuthFirebase()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val modoRegistroInicial = intent.getBooleanExtra("modoRegistro", false)
        setContent {
            StocklfyTheme {
                LoginFormulario(
                    modoRegistroInicial = modoRegistroInicial,
                    onLoginClick = { email, password ->
                        loginUsuario(email, password)
                    },
                    onRegisterClick = { email, password ->
                        registrarUsuario(email, password)
                    },
                    onVolverClick = {
                        finish()
                    }
                )
            }
        }
    }

    private fun loginUsuario(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        authFirebase.login(email = email, password = password, onSuccess = {
            Toast.makeText(this, "Inicio de sesión correcto", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()
        }, onError = { mensaje ->
            Toast.makeText(this, "No se ha encontrado ningún usuario con ese correo", Toast.LENGTH_LONG).show()
        })
    }

    private fun registrarUsuario(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos 6 carácteres",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        authFirebase.register(
            email = email,
            password = password,
            onSuccess = { uid, emailUser ->
                val usuarioFirestore = UsuarioFirestore()
                usuarioFirestore.guardarUsuario(uid, emailUser)
                CategoriasFirestore().crearCategoriasPorDefecto(
                    uid = uid,
                    onSuccess = {
                        Toast.makeText(this, "Se ha creado correctamente el usuario", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    },
                    onError = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                    }
                )
            },
            onError = { mensaje ->
                Toast.makeText(this, "No se ha podido crear el usuario/usuario ya registrado", Toast.LENGTH_LONG).show()
            }
        )
    }


    @Composable
    fun LoginFormulario(
        modoRegistroInicial: Boolean,
        onLoginClick: (String, String) -> Unit,
        onRegisterClick: (String, String) -> Unit,
        onVolverClick: () -> Unit
    ) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var cargando by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }
        var modoRegistro by remember { mutableStateOf(modoRegistroInicial) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE7D4DC)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF111322)
                ),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (modoRegistro) "Crear cuenta" else "Iniciar sesión",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (modoRegistro)
                            "Regístrate para empezar a gestionar tu inventario"
                        else
                            "Accede a tu cuenta de Stocklfy",
                        color = Color(0xFFBFC3D9),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF5C6BC0),
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            val image = if (passwordVisible) {
                                Icons.Filled.Visibility
                            } else {
                                Icons.Filled.VisibilityOff
                            }

                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = null
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color(0xFF5C6BC0),
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = {
                            cargando = true

                            if (modoRegistro) {
                                onRegisterClick(email, password)
                            } else {
                                onLoginClick(email, password)
                            }

                            cargando = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (modoRegistro) "Registrarse" else "Entrar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onVolverClick,
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(56.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Volver",
                            fontSize = 22.sp
                        )
                    }
                    if (cargando) {
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}