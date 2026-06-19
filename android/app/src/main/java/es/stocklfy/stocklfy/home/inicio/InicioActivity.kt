package es.stocklfy.stocklfy.home.inicio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun PantallaInicio(
    nombreUsuario: String = "Usuario",
    totalProductos: Int = 0,
    productosCaducan: Int = 0,
    totalCaducados: Int = 0,
    onAgregarProductoClick: () -> Unit = {},
    onVerInventarioClick: () -> Unit = {},
    onLectorTicketClick: () -> Unit = {}
)   {
    val consejos = listOf(
        "Revisa los productos próximos a caducar para evitar desperdiciar comida.",
        "Organiza tu inventario por fecha para consumir primero lo más antiguo.",
        "Añade notas a los productos para recordar su uso.",
        "Usa el lector de tickets para ahorrar tiempo al registrar compras.",
        "Revisa tu inventario una vez por semana."
    )

    var consejoActual by remember { mutableStateOf(consejos.random()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60000)
            consejoActual = consejos.random()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "¡Bienvenido, $nombreUsuario!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Resumen de tu inventario",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ResumenDashboardCard(
                titulo = "Productos",
                valor = totalProductos.toString(),
                icono = Icons.Default.Inventory2,
                modifier = Modifier.weight(1f)

            )

            ResumenDashboardCard(
                titulo = "Caducan",
                valor = productosCaducan.toString(),
                icono = Icons.Default.Warning,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResumenDashboardCard(
                titulo = "Caducados",
                valor = totalCaducados.toString(),
                icono = Icons.Default.Error,
                modifier = Modifier.weight(1f)
            )

            ResumenDashboardCard(
                titulo = "Compras",
                valor = "Lector",
                icono = Icons.Default.ReceiptLong,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Acciones rápidas",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        AccionDashboard(
            icono = Icons.Default.AddShoppingCart,
            titulo = "Añadir un producto",
            subtitulo = "Registra un producto manualmente",
            onClick = onAgregarProductoClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        AccionDashboard(
            icono = Icons.Default.Inventory2,
            titulo = "Ver inventario",
            subtitulo = "Consulta todos tus productos guardados",
            onClick = onVerInventarioClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        AccionDashboard(
            icono = Icons.Default.ReceiptLong,
            titulo = "Lector de tickets",
            subtitulo = "Escanea un ticket con OCR",
            onClick = onLectorTicketClick
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Consejo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = consejoActual,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun ResumenDashboardCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    val colorCard = when (titulo) {
        "Productos" -> Color(0xFF42A5F5)
        "Caducan" -> Color(0xFFFFB300)
        "Caducados" -> Color(0xFFEF5350)
        "Compras" -> Color(0xFF66BB6A)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorCard.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(24.dp),
                    tint = colorCard
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = valor,
                    fontSize = if (valor.all { it.isDigit() }) 24.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Text(
                    text = titulo,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AccionDashboard(
    icono: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    val colorAccion = when (titulo) {
        "Añadir un producto" -> Color(0xFF42A5F5)
        "Ver inventario" -> Color(0xFF66BB6A)
        "Lector de tickets" -> Color(0xFFAB47BC)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = colorAccion.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(26.dp),
                    tint = colorAccion
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitulo,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorAccion
            )
        }
    }
}