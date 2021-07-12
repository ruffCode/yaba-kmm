/*
 * Copyright 2021 Alexi Bre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.alexib.yaba.android.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.android.ui.theme.YabaTheme

@Composable
fun GenericDialog(
    modifier: Modifier = Modifier,
    title: String,
    positiveAction: DialogAction,
    negativeAction: DialogAction,
    description: String? = null,
    warnConfirm: Boolean? = false,
) {
    AlertDialog(
        onDismissRequest = {
            negativeAction.onAction()
        },
        modifier = modifier.padding(16.dp),
        title = { Text(text = title) },
        text = {
            description?.let {
                Text(text = it)
            }
        },
        confirmButton = {
            val danger = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                backgroundColor = MaterialTheme.colors.error
            )
            OutlinedButton(
                onClick = { positiveAction.onAction() },
                colors = if (warnConfirm == true) danger else ButtonDefaults.buttonColors()
            ) {
                Text(text = positiveAction.buttonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { negativeAction.onAction() },

                ) {
                Text(text = negativeAction.buttonText)
            }
        },

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

@Preview
@Composable
fun GenericDialogPreview() {
    YabaTheme {
        GenericDialog(
            title = "Alert",
            negativeAction = NegativeAction() {},
            positiveAction = PositiveAction() {},
            warnConfirm = true
        )
    }
}
