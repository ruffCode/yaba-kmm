package tech.alexib.yaba.android.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import tech.alexib.yaba.android.R

@Composable
fun BackArrowButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = modifier
    ) {
        Icon(Icons.Filled.ArrowBack, stringResource(R.string.back_arrow))
    }
}
