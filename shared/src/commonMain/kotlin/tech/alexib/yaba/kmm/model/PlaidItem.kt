package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid
import kotlin.jvm.JvmInline

@JvmInline
value class PlaidItemId(val value: Uuid)

interface PlaidItemBase {
    val id: PlaidItemId
    val plaidInstitutionId: PlaidInstitutionId
    val status: String
    val name: String
}

data class PlaidItem(
    override val id: PlaidItemId,
    override val plaidInstitutionId: PlaidInstitutionId,
    override val status: String,
    override val name: String
) : PlaidItemBase


data class PlaidItemWIthAccounts(
    override val id: PlaidItemId,
    override val plaidInstitutionId: PlaidInstitutionId,
    override val status: String,
    override val name: String,
    val accounts: List<Account>,
) : PlaidItemBase