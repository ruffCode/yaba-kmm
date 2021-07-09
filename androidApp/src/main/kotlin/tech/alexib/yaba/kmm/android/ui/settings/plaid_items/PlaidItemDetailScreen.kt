package tech.alexib.yaba.kmm.android.ui.settings.plaid_items

import android.os.Parcelable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.getViewModel
import tech.alexib.yaba.kmm.android.ui.components.BankLogo
import tech.alexib.yaba.kmm.android.util.base64ToBitmap
import tech.alexib.yaba.kmm.model.AccountSubtype
import tech.alexib.yaba.kmm.model.AccountType
import tech.alexib.yaba.kmm.model.PlaidItemWithAccounts

@Composable
fun PlaidItemDetailScreen(
    item: PlaidItemDetail,
    handleBack: () -> Unit,
) {
    val viewModel: PlaidItemDetailScreenViewModel = getViewModel()
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically)
            ) {
                IconButton(
                    onClick = handleBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back arrow")
                }
                BankLogo(
                    modifier = Modifier.align(Alignment.TopCenter),
                    logoBitmap = base64ToBitmap(item.base64Logo)
                )
                TextButton(
                    onClick = {
                        viewModel.unlinkItem(item.id)
                        handleBack()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp)
                ) {
                    Text(text = "Unlink")
                }
            }
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlaidItemDetailScreen(
            item = item,
            setAccountHidden = { hidden, accountId ->
                viewModel.setAccountHidden(hidden, accountId)
            }
        )
    }
}

@Composable
private fun PlaidItemDetailScreen(
    item: PlaidItemDetail,
    setAccountHidden: (Boolean, Uuid) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(text = item.name, style = MaterialTheme.typography.h4)
                }
            }
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(item.accounts) { account ->
                    AccountItemDetail(account) { accountId, hidden ->
                        setAccountHidden(hidden, accountId)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountItemDetail(account: PlaidItemDetail.Account, onChange: (Uuid, Boolean) -> Unit) {
    val hidden = remember { mutableStateOf(account.hidden) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
    ) {
        val rowModifier = Modifier
            .fillMaxWidth()

            .wrapContentHeight(Alignment.CenterVertically)
            .padding(vertical = 16.dp)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = "${account.name}  ****${account.mask}",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = "Account type",
                    style = MaterialTheme.typography.body1
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = account.subtype.name,
                    style = MaterialTheme.typography.body1
                )
            }
        }
        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show",
                style = MaterialTheme.typography.body1
            )
            Switch(
                checked = !hidden.value,
                onCheckedChange = {
                    onChange(account.id, !it)
                    hidden.value = !it
                }
            )
        }
        Divider()
    }
}

@Suppress("ConstructorParameterNaming")
@Parcelize
data class PlaidItemDetail(
    val id: Uuid,
    val name: String,
    val base64Logo: String,
    val accounts: List<Account>,
) : Parcelable {

    @Parcelize
    data class Account(
        val id: Uuid,
        val name: String,
        val mask: String,
        val type: AccountType,
        val subtype: AccountSubtype,
        val hidden: Boolean = false,
    ) : Parcelable

    companion object {
        operator fun invoke(item: PlaidItemWithAccounts): PlaidItemDetail = with(item) {
            PlaidItemDetail(
                id = id,
                name = name,
                base64Logo = base64Logo,
                accounts = accounts.map {
                    Account(
                        id = it.id,
                        name = it.name,
                        mask = it.mask,
                        type = it.type,
                        subtype = it.subtype,
                        hidden = it.hidden
                    )
                }
            )
        }
    }
}
