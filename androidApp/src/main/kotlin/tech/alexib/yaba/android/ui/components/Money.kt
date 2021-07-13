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

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.util.moneyFormat

@Composable
fun Money(amount: Double, modifier: Modifier = Modifier, textStyle: TextStyle = TextStyle.Default) {
    val formatted = if (amount == 0.0) "$0.00" else "$${moneyFormat.format(amount)}"
    val color = if (amount < 0) Color.Red else MoneyGreen
    Text(text = formatted, color = color, modifier = modifier, style = textStyle)
}
