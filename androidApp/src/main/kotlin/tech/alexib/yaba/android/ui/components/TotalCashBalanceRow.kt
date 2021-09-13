package tech.alexib.yaba.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.android.R

@Composable
fun TotalCashBalanceRow(
    balance: Double?
) {
    Card(
        modifier = Modifier
            .wrapContentHeight(Alignment.CenterVertically),
        elevation = 3.dp
    ) {
        BalanceRow(
            balance = balance ?: 0.0,
            description = stringResource(id = R.string.current_cash_balance),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun TotalCashBalanceRowPreview() {
    TotalCashBalanceRow(39000.00)
}
