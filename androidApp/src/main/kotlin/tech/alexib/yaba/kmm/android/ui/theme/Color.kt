package tech.alexib.yaba.kmm.android.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

val Green500 = Color(0xFF009688)
val IndigoLight = Color(0xFF8f9bff)
val Red700 = Color(0xFFd32f2f)
val PinkLight = Color(0xFFff79b0)
val BlueSlate = Color(0xFF4f83cc)
val MoneyGreen = Color(0xFF529c64)
val ErrorDark = Color(0xFFB00020)
val ErrorLight = Color(0xFFEF5350)

/**
 * Return the fully opaque color that results from compositing [onSurface] atop [surface] with the
 * given [alpha]. Useful for situations where semi-transparent colors are undesirable.
 */
@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(surface)
}
