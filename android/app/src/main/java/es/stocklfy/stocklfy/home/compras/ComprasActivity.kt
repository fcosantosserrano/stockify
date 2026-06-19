package es.stocklfy.stocklfy.home.compras

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color

@Composable
fun PantallaCompras(
    onLectorClick: () -> Unit = {},
    onManualClick: () -> Unit = {},
    onCodigoBarrasClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Compras",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        SeccionCompras {
            OpcionCompra(
                icon = Icons.Default.CameraAlt,
                titulo = "Lector de tickets",
                subtitulo = "Escanea tickets automáticamente",
                onClick = onLectorClick
            )

            HorizontalDivider()

            OpcionCompra(
                icon = Icons.Default.Edit,
                titulo = "Añadir manualmente",
                subtitulo = "Introduce los productos a mano",
                onClick = onManualClick
            )

            HorizontalDivider()

            OpcionCompra(
                icon = Icons.Default.QrCodeScanner,
                titulo = "Código de barras",
                subtitulo = "Escanea un producto con la cámara",
                onClick = onCodigoBarrasClick
            )
        }
    }
}

@Composable
fun SeccionCompras(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun OpcionCompra(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    val colorAccion = when (titulo) {
        "Lector de tickets" -> Color(0xFFAB47BC)
        "Añadir manualmente" -> Color(0xFF42A5F5)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Surface(
            color = colorAccion.copy(alpha = 0.15f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .size(26.dp),
                tint = colorAccion
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = subtitulo,
                fontSize = 13.sp,
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