package es.stocklfy.stocklfy.global

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun capitalizarPrimera(texto: String): String {
    return texto.lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase() else it.toString()
    }
}

fun capitalizarPrimeraEnTiempoReal(nuevo: TextFieldValue): TextFieldValue {
    val textoFormateado = if (nuevo.text.isNotEmpty()) {
        nuevo.text.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    } else {
        nuevo.text
    }

    return TextFieldValue(
        text = textoFormateado,
        selection = TextRange(textoFormateado.length)
    )
}

fun normalizarCategoria(categoria: String): String {
    return categoria
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
        .replace(" ", "_")
}