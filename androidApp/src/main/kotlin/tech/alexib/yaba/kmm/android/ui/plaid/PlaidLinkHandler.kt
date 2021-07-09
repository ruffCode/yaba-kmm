package tech.alexib.yaba.kmm.android.ui.plaid

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import com.plaid.link.OpenPlaidLink
import com.plaid.link.PlaidActivityResultContract
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkResult

@OptIn(PlaidActivityResultContract::class)
@Composable
fun PlaidLinkHandler(
    onResult: (LinkResult) -> Unit,
    content: @Composable (ActivityResultLauncher<LinkTokenConfiguration>) -> Unit
) {
    val linkLauncher = rememberLauncherForActivityResult(contract = OpenPlaidLink()) {
        onResult(it)
    }
    content(linkLauncher)
}
