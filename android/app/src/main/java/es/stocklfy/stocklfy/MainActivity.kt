package es.stocklfy.stocklfy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.home.HomeActivity
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            StocklfyTheme {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    PantallaInicio(
                        onEntrarClick = {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("modoRegistro", false)
                            startActivity(intent)
                        },
                        onRegistroClick = {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.putExtra("modoRegistro", true)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaInicio(
    onEntrarClick: () -> Unit,
    onRegistroClick: () -> Unit
) {
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
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(42.dp))
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(
                            Color.White,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logoprincipal),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Stocklfy",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gestiona tus productos fácilmente",
                    color = Color(0xFFBFC3D9),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onEntrarClick,
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C6BC0)
                    )
                ) {
                    Text(
                        text = "Entrar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))
                OutlinedButton(
                    onClick = onRegistroClick,
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Registrarse",
                        fontSize = 22.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF5C6BC0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        verticalArrangement = Arrangement.Center,
                        Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Controla",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,

                        )
                        Text(
                            text = "Caducidades y compras",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Evita desperdiciar comida",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}