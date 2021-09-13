package tech.alexib.yaba.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.util.moneyFormat

@Composable
fun BalanceRow(
    balance: Double,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(text = description)
            Text(
                text = "$${moneyFormat.format(balance)}",
                color = if (balance > 0) MoneyGreen else Color.Red,
                textAlign = TextAlign.End
            )
        }
    }
}
