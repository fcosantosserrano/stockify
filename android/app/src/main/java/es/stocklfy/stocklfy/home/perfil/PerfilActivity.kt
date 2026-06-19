package es.stocklfy.stocklfy.home.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.stocklfy.stocklfy.ui.components.BotonCerrarSesion
import es.stocklfy.stocklfy.ui.components.BotonSecundario

@Composable
fun PantallaPerfil(
    nombre: String,
    email: String,
    notificacionesActivas: Boolean,
    onEditarPerfilClick: () -> Unit,
    onCambiarContrasenaClick: () -> Unit,
    onAjustesClick: () -> Unit,
    onNotificacionesChange: (Boolean) -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        PerfilHeader(
            nombre = nombre,
            email = email,
            onEditarPerfilClick = onEditarPerfilClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        SeccionCard(titulo = "Cuenta") {
            OpcionPerfil(
                icon = { Icon(Icons.Default.Lock, contentDescription = null) },
                titulo = "Cambiar contraseña",
                subtitulo = "Actualiza tu contraseña de acceso",
                onClick = onCambiarContrasenaClick
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SeccionCard(titulo = "Preferencias") {
            OpcionConSwitch(
                icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                titulo = "Notificaciones",
                subtitulo = "Activar avisos y recordatorios",
                checked = notificacionesActivas,
                onCheckedChange = onNotificacionesChange
            )

            HorizontalDivider()

            OpcionPerfil(
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                titulo = "Ajustes",
                subtitulo = "Configuración general de la aplicación",
                onClick = onAjustesClick
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        BotonCerrarSesion(
            onClick = onLogoutClick,
            modifier = Modifier
                .width(220.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                Icons.Default.Logout,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión")
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PerfilHeader(
    nombre: String,
    email: String,
    onEditarPerfilClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = nombre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = onEditarPerfilClick) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar perfil")
            }
        }
    }
}

@Composable
fun SeccionCard(
    titulo: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = titulo,
                modifier = Modifier.padding(20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
fun OpcionPerfil(
    icon: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OpcionConSwitch(
    icon: @Composable () -> Unit,
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            shape = CircleShape
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = titulo,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}