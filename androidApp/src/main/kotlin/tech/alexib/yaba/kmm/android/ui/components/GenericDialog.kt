package tech.alexib.yaba.kmm.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme

@Composable
fun GenericDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    description: String? = null,
    positiveAction: DialogAction? = null,
    negativeAction: DialogAction? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(text = title) },
        text = {
            description?.let {
                Text(text = it)
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                negativeAction?.let {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onError
                        ),
                        onClick = it.onAction
                    ) {
                        Text(text = it.buttonText)
                    }
                }
                positiveAction?.let {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = it.onAction
                    ) {
                        Text(text = it.buttonText)
                    }
                }
            }
        }
    )
}

interface DialogAction {
    val buttonText: String
    val onAction: () -> Unit
}

data class PositiveAction(
    override val buttonText: String = "Confirm",
    override val onAction: () -> Unit,
) : DialogAction

data class NegativeAction(
    override val buttonText: String = "Cancel",
    override val onAction: () -> Unit,
) : DialogAction

data class GenericDialogInfo(
    val title: String,
    val description: String? = null,
    val positiveAction: DialogAction? = null,
    val negativeAction: DialogAction? = null,
    val onDismiss: () -> Unit,
)

@Preview
@Composable
fun GenericDialogPreview() {
    YabaTheme {
        GenericDialog(onDismiss = { }, title = "Alert")
    }
}
