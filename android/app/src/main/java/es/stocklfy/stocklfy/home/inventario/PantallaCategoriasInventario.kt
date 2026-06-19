package es.stocklfy.stocklfy.home.inventario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import es.stocklfy.stocklfy.ui.components.BotonSecundario

data class CategoriaInventario(
    val nombre: String,
    val descripcion: String,
    val icono: ImageVector
)

fun categoriaDesdeNombre(nombre: String): CategoriaInventario {
    return when (nombre) {
        "Lácteos" -> CategoriaInventario("Lácteos", "Leche, queso, yogures", Icons.Default.LocalDrink)
        "Carnes" -> CategoriaInventario("Carnes", "Pollo, ternera, cerdo", Icons.Default.Restaurant)
        "Pescados" -> CategoriaInventario("Pescados", "Pescado y marisco", Icons.Default.SetMeal)
        "Frutas" -> CategoriaInventario("Frutas", "Fruta fresca", Icons.Default.Eco)
        "Verduras" -> CategoriaInventario("Verduras", "Verduras y hortalizas", Icons.Default.Grass)
        "Bebidas" -> CategoriaInventario("Bebidas", "Agua, zumos, refrescos", Icons.Default.LocalBar)
        "Panadería" -> CategoriaInventario("Panadería", "Pan y bollería", Icons.Default.BakeryDining)
        "Despensa" -> CategoriaInventario("Despensa", "Pasta, arroz, conservas", Icons.Default.Inventory2)
        "Congelados" -> CategoriaInventario("Congelados", "Productos congelados", Icons.Default.AcUnit)
        else -> CategoriaInventario("Otros", "Sin categoría", Icons.Default.Category)
    }
}

fun obtenerColorCategoriaInventario(categoria: String): Color {
    return when (categoria) {
        "Lácteos" -> Color(0xFF42A5F5)
        "Carnes" -> Color(0xFFEF5350)
        "Pescados" -> Color(0xFF26A69A)
        "Frutas" -> Color(0xFFFFCA28)
        "Verduras" -> Color(0xFF66BB6A)
        "Bebidas" -> Color(0xFF26C6DA)
        "Panadería" -> Color(0xFFFFB74D)
        "Despensa" -> Color(0xFF8D6E63)
        "Congelados" -> Color(0xFF7E57C2)
        else -> Color(0xFF5C6BC0)
    }
}
@Composable
fun PantallaCategoriasInventario(
    categorias: List<CategoriaInventario>,
    onCategoriaClick: (CategoriaInventario) -> Unit = {},
    onBackClick: () -> Unit = {}
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
            text = "Selecciona una categoría para ver sus productos",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (categorias.isEmpty()) {
            Text(
                text = "No hay categorías con productos todavía.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categorias) { categoria ->
                    CategoriaCard(
                        categoria = categoria,
                        onClick = { onCategoriaClick(categoria) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver",
            onClick = onBackClick,
            modifier = Modifier
                .width(220.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun CategoriaCard(
    categoria: CategoriaInventario,
    onClick: () -> Unit
) {
    val colorCategoria = obtenerColorCategoriaInventario(categoria.nombre)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = categoria.icono,
                contentDescription = categoria.nombre,
                modifier = Modifier.size(38.dp),
                tint = colorCategoria
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = categoria.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorCategoria
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = categoria.descripcion,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}