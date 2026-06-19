package es.stocklfy.stocklfy.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.LoginActivity
import es.stocklfy.stocklfy.MainActivity
import es.stocklfy.stocklfy.PantallaBienvenida
import es.stocklfy.stocklfy.R
import es.stocklfy.stocklfy.firebase.AuthFirebase
import es.stocklfy.stocklfy.firebase.firestore.opciones.ProductosFirestore
import es.stocklfy.stocklfy.firebase.firestore.opciones.UsuarioFirestore
import es.stocklfy.stocklfy.home.compras.PantallaCompras
import es.stocklfy.stocklfy.home.compras.funcionalidades.AgregarProductoActivity
import es.stocklfy.stocklfy.home.compras.funcionalidades.AgregarProductoCodigoBarras
import es.stocklfy.stocklfy.home.compras.funcionalidades.LectorTicketsActivity
import es.stocklfy.stocklfy.home.inicio.PantallaInicio
import es.stocklfy.stocklfy.home.inventario.CategoriaInventarioActivity
import es.stocklfy.stocklfy.home.inventario.funcionalidades.PantallaCaducidadActivity
import es.stocklfy.stocklfy.home.inventario.PantallaInventarioActivity
import es.stocklfy.stocklfy.home.inventario.PantallaTodosProductosActivity
import es.stocklfy.stocklfy.home.inventario.funcionalidades.PantallaBuscarProductoActivity
import es.stocklfy.stocklfy.home.inventario.funcionalidades.PantallaCaducadosActivity
import es.stocklfy.stocklfy.home.perfil.Funcionalidades.CambiarPasswordActivity
import es.stocklfy.stocklfy.home.perfil.Funcionalidades.EditarPerfilActivity
import es.stocklfy.stocklfy.home.perfil.PantallaPerfil
import es.stocklfy.stocklfy.ui.theme.StocklfyTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StocklfyTheme {
                HomeActivityApp()
            }
        }
    }
}

@Composable
fun HomeActivityApp() {

    val productosFirestore = ProductosFirestore()
    val authFirebase = AuthFirebase()
    val usuarioFirestore = UsuarioFirestore()

    var totalProductos by remember { mutableStateOf(0) }
    var totalCaducan by remember { mutableStateOf(0) }
    var totalCaducados by remember { mutableStateOf(0) }
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.INICIO) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Sin email"
    val uid = user?.uid

    var nombre by rememberSaveable {
        mutableStateOf(email.substringBefore("@"))
    }

    fun cargarNombre() {
        if (uid != null) {
            usuarioFirestore.obtenerNombreUsuario(
                uid = uid,
                onSuccess = { nombreObtenido ->
                    nombre = nombreObtenido
                },
                onError = {
                    nombre = email.substringBefore("@")
                }
            )
        }
    }

    val editarPerfilLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cargarNombre()
        }
    }

    LaunchedEffect(uid) {
        cargarNombre()
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = it.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.INICIO -> PantallaInicio(
                nombreUsuario = nombre,
                totalProductos = totalProductos,
                productosCaducan = totalCaducan,
                totalCaducados = totalCaducados,

                onAgregarProductoClick = {
                    val intent = Intent(context, AgregarProductoActivity::class.java)
                    context.startActivity(intent)
                },
                onVerInventarioClick = {
                    val intent = Intent(context, CategoriaInventarioActivity::class.java)
                    context.startActivity(intent)
                },
                onLectorTicketClick = {
                    val intent = Intent(context, LectorTicketsActivity::class.java)
                    context.startActivity(intent)
                }
            )

            AppDestinations.COMPRAS -> PantallaCompras(
                onLectorClick = {
                    val intent = Intent(context, LectorTicketsActivity::class.java)
                    context.startActivity(intent)
                },
                onManualClick = {
                    val intent = Intent(context, AgregarProductoActivity::class.java)
                    context.startActivity(intent)
                },
                onCodigoBarrasClick = {
                    val intent = Intent(context, AgregarProductoCodigoBarras::class.java)
                    context.startActivity(intent)
                }
            )

            AppDestinations.INVENTARIO -> PantallaInventarioActivity(
                onCategoriasClick = {
                    val intent = Intent(context, CategoriaInventarioActivity::class.java)
                    context.startActivity(intent)
                },
                onTodosProductosClick = {
                    val intent = Intent(context, PantallaTodosProductosActivity::class.java)
                    context.startActivity(intent)
                },
                onCaducidadClick = {
                    val intent = Intent(context, PantallaCaducidadActivity::class.java)
                    context.startActivity(intent)
                },
                onCaducadosClick = {
                    val intent = Intent(context, PantallaCaducadosActivity::class.java)
                    context.startActivity(intent)
                },
                onBuscarClick = {
                    val intent = Intent(context, PantallaBuscarProductoActivity::class.java)
                    context.startActivity(intent)
                }
            )

            AppDestinations.PROFILE -> PantallaPerfil(
                nombre = nombre,
                email = email,
                notificacionesActivas = true,
                onEditarPerfilClick = {
                    val intent = Intent(context, EditarPerfilActivity::class.java)
                    editarPerfilLauncher.launch(intent)
                },
                onCambiarContrasenaClick = {
                    val intent = Intent(context, CambiarPasswordActivity::class.java)
                    context.startActivity(intent)
                },
                onAjustesClick = {
                    Toast.makeText(context, "Ajustes", Toast.LENGTH_SHORT).show()
                },
                onNotificacionesChange = { activadas ->
                    Toast.makeText(
                        context,
                        if (activadas) "Notificaciones activadas" else "Notificaciones desactivadas",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onLogoutClick = {
                    Toast.makeText(
                        context,
                        "Sesión cerrada",
                        Toast.LENGTH_SHORT
                    ).show()

                    authFirebase.logout()

                    val intent = Intent(
                        context,
                        MainActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    context.startActivity(intent)
                }
            )
        }
    }

    LaunchedEffect(uid) {

        if (uid != null) {

            productosFirestore.contarProductos(
                uid = uid,
                onSuccess = {
                    totalProductos = it
                },
                onError = {}
            )

            productosFirestore.contarProductosCaducan(
                uid = uid,
                diasLimite = 7,
                onSuccess = {
                    totalCaducan = it
                },
                onError = {}
            )

            productosFirestore.contarProductosCaducados(
                uid = uid,
                onSuccess = {
                    totalCaducados = it
                },
                onError = {}
            )
        }
    }

}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    INICIO("Inicio", R.drawable.ic_home),
    COMPRAS("Compras", R.drawable.image),
    INVENTARIO("Inventario", R.drawable.ic_home),
    PROFILE("Perfil", R.drawable.ic_account_box),
}