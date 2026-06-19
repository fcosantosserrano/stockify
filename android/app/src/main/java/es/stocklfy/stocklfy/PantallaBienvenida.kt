package es.stocklfy.stocklfy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class BienvenidaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StocklfyTheme {
                PantallaBienvenida(
                    onLoginClick = {

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

@Composable
fun PantallaBienvenida(
    onLoginClick: () -> Unit,
    onRegistroClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE9A0B5)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(310.dp)
                .height(620.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF10111F)
            ),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = Color(0xFF5C6BC0),
                        modifier = Modifier.padding(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Stocklfy",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestiona tu comida fácilmente",
                    color = Color(0xFFB8B8C8),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(90.dp))
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .width(160.dp)
                        .height(42.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5A5F),
                        contentColor = Color.White
                    )
                ) {
                    Text("Iniciar sesión")
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onRegistroClick,
                    modifier = Modifier
                        .width(160.dp)
                        .height(42.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF10111F)
                    )
                ) {
                    Text("Registrarse")
                }
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFF5A5F),
                                        Color.White
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = "Evita",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "desperdiciar comida",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Controla caducidades",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}