package es.stocklfy.stocklfy.home.inventario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color

@Composable
fun PantallaInventarioActivity(
    onCategoriasClick: () -> Unit = {},
    onTodosProductosClick: () -> Unit = {},
    onCaducidadClick: () -> Unit = {},
    onCaducadosClick: () -> Unit = {},
    onBuscarClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Inventario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Consulta y organiza los productos que tienes guardados",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        SeccionInventario {
            OpcionInventario(
                icon = Icons.Default.Category,
                titulo = "Ver por categorías",
                subtitulo = "Organiza tus productos por tipo: lácteos, bebidas, carnes...",
                onClick = onCategoriasClick
            )
            HorizontalDivider()
            OpcionInventario(
                icon = Icons.Default.Inventory2,
                titulo = "Ver todos los productos",
                subtitulo = "Muestra la lista completa de productos guardados",
                onClick = onTodosProductosClick
            )
            HorizontalDivider()
            OpcionInventario(
                icon = Icons.Default.Warning,
                titulo = "Próximos a caducar",
                subtitulo = "Consulta alimentos con fecha de caducidad cercana",
                onClick = onCaducidadClick
            )
            HorizontalDivider()
            OpcionInventario(
                icon = Icons.Default.Error,
                titulo = "Productos caducados",
                subtitulo = "Consulta los alimentos que se están caducados",
                onClick = onCaducadosClick
            )
            HorizontalDivider()
            OpcionInventario(
                icon = Icons.Default.Search,
                titulo = "Buscar producto",
                subtitulo = "Encuentra rápidamente un producto en tu inventario",
                onClick = onBuscarClick
            )
        }
    }
}

@Composable
fun SeccionInventario(
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
fun OpcionInventario(
    icon: ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    val colorAccion = when (titulo) {
        "Ver por categorías" -> Color(0xFF7E57C2)
        "Ver todos los productos" -> Color(0xFF42A5F5)
        "Próximos a caducar" -> Color(0xFFFFB300)
        "Productos caducados" -> Color(0xFFEF5350)
        "Buscar producto" -> Color(0xFF26A69A)
        else -> MaterialTheme.colorScheme.primary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = colorAccion.copy(alpha = 0.15f),
            shape = CircleShape
        ) {
            Icon(
                imageVector = icon,
                contentDescription = titulo,
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
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

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