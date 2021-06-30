package tech.alexib.yaba.kmm.android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.benasher44.uuid.uuidFrom
import kotlinx.datetime.LocalDate
import tech.alexib.yaba.kmm.android.ui.home.moneyFormat
import tech.alexib.yaba.kmm.android.ui.theme.MoneyGreen
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.model.Transaction
import tech.alexib.yaba.kmm.model.TransactionType

private val transactionStub = Transaction(
    name = "ACH Electronic CreditGUSTO PAY 123456",
    id = uuidFrom("c8833368-75f7-4019-98d0-83b680b5dd8d"),
    type = TransactionType.SPECIAL,
    amount = 50000.0,
    date = LocalDate.parse("2021-04-25"),
    accountId = uuidFrom("6e49eb05-af5f-4f2e-9d9d-6d98117a602f"),
    itemId = uuidFrom("99dd6382-0b02-46c5-aaa4-ada87c505443"),
    category = "Travel",
    subcategory = "Airlines and Aviation Services",
    isoCurrencyCode = "USD",
    pending = true
)


@Composable
fun TransactionListItem(
    transaction: Transaction,
) {
    ListItem(modifier = Modifier.fillMaxWidth(),

        trailing = {
            Text(
                text = "$${moneyFormat.format(transaction.amount)}",
                style = MaterialTheme.typography.body2,
            )
        },
        singleLineSecondaryText = true, secondaryText = {
            Text(
                text = transaction.pending?.let { if (it) "Pending" else "" } ?: "",
                style = MaterialTheme.typography.caption,
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.ReceiptLong,
                contentDescription = "receipt",
                tint = Color.Green,
                modifier = Modifier
                    .size(45.dp)
                    .padding(4.dp)
            )
        }) {

        Text(
            text = transaction.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.body2,
        )

    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    isLast: Boolean = false,
) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
                    text = transaction.pending?.let { if (it) "Pending" else "" } ?: "",
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

        if (!isLast) {
            Divider(modifier = Modifier.align(Alignment.BottomCenter))
        }

    }
}

@Preview
@Composable
fun TransactionItemPreview() {
    YabaTheme(darkTheme = true) {
        TransactionItem(transaction = transactionStub)
    }
}


@Preview
@Composable
fun TransactionListItemPreview() {
    YabaTheme {
        TransactionListItem(transaction = transactionStub)
    }
}