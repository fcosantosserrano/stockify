package es.stocklfy.stocklfy.home.inventario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import es.stocklfy.stocklfy.firebase.firestore.opciones.CategoriasFirestore
import es.stocklfy.stocklfy.home.inventario.ui.theme.StocklfyTheme

class CategoriaInventarioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StocklfyTheme {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                var categorias by remember {
                    mutableStateOf<List<CategoriaInventario>>(emptyList())
                }

                LaunchedEffect(Unit) {
                    if (uid != null) {
                        CategoriasFirestore().obtenerCategoriasConProductos(
                            uid = uid,
                            onSuccess = { nombres ->
                                categorias = nombres.map { nombre ->
                                    categoriaDesdeNombre(nombre)
                                }
                            },
                            onError = { error ->
                                Toast.makeText(
                                    this@CategoriaInventarioActivity,
                                    error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }

                PantallaCategoriasInventario(
                    categorias = categorias,
                    onCategoriaClick = { categoria ->
                        val intent = Intent(this, PantallaTodosProductosActivity::class.java)
                        intent.putExtra("categoria", categoria.nombre)
                        startActivity(intent)
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}