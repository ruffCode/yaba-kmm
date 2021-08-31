package tech.alexib.yaba.android.ui.accounts.detail

import com.benasher44.uuid.Uuid

sealed class AccountDetailScreenAction {
    object NavigateBack : AccountDetailScreenAction()
    data class OnTransactionSelected(val transactionId: Uuid) : AccountDetailScreenAction()
}
