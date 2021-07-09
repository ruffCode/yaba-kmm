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
