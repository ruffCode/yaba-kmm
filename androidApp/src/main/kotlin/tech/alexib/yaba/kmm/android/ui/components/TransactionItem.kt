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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.benasher44.uuid.Uuid
import tech.alexib.yaba.kmm.android.ui.theme.MoneyGreen
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.android.util.moneyFormat
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionStubs

@Composable
fun TransactionItem(
    transaction: Transaction,
    onSelected: ((Uuid) -> Unit)? = null
) {
    val modifier = Modifier
        .height(60.dp)
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .clickable {
            onSelected?.let { it(transaction.id) }
        }

    Box(
        modifier = modifier
    ) {
        TransactionItemContent(transaction = transaction)

        Divider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Suppress("LongMethod")
@Composable
private fun TransactionItemContent(
    transaction: Transaction,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val (iconRef, nameRef, amountRef, pendingRef) = createRefs()

        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = "receipt",
            tint = MoneyGreen,
            modifier = Modifier
                .size(45.dp)
                .padding(4.dp)
                .constrainAs(iconRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = transaction.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(0.62f)
                    .constrainAs(nameRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(iconRef.end, margin = 8.dp)
                    }
            )
        }
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = transaction.pending.let { if (it) "Pending" else "" },
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .constrainAs(pendingRef) {
                        top.linkTo(nameRef.bottom, margin = 4.dp)
                        bottom.linkTo(parent.bottom, margin = 8.dp)
                        start.linkTo(iconRef.end, margin = 8.dp)
                    },
            )
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = "$${moneyFormat.format(transaction.amount)}",
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(0.2f)
                    .constrainAs(amountRef) {
                        end.linkTo(parent.end, margin = 4.dp)

                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                textAlign = TextAlign.End,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun TransactionItemPreview() {
    YabaTheme(darkTheme = true) {
        TransactionItem(transaction = TransactionStubs.transactionStub, null)
    }
}
