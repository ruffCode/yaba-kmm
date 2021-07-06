package tech.alexib.yaba.kmm.data.api.dto

import com.benasher44.uuid.Uuid
import kotlinx.datetime.LocalDate
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.kmm.model.TransactionType

data class TransactionDto(
    val id: Uuid,
    val name: String,
    val type: TransactionType,
    val amount: Double,
    val date: LocalDate,
    val accountId: Uuid,
    val itemId: Uuid,
    val category: String?,
    val subcategory: String?,
    val isoCurrencyCode: String?,
    val pending: Boolean?,
    val merchantName: String? = null
)

internal fun TransactionDto.toEntity() = TransactionEntity(
    name = name,
    id = id,
    type = type,
    amount = amount,
    date = date,
    account_id = accountId,
    item_id = itemId,
    category = category,
    pending = pending,
    subcategory = subcategory,
    iso_currency_code = isoCurrencyCode,
    merchant_name = merchantName
)

internal fun tech.alexib.yaba.fragment.Transaction.toDto() = TransactionDto(
    name = name,
    id = id as Uuid,
    type = TransactionType.valueOf(type.uppercase()),
    amount = amount,
    date = date as LocalDate,
    accountId = accountId as Uuid,
    itemId = itemId as Uuid,
    category = category,
    pending = pending,
    subcategory = subcategory,
    isoCurrencyCode = isoCurrencyCode,
    merchantName = merchantName
)

internal fun List<TransactionDto>.toEntities():List<TransactionEntity> = this.map { it.toEntity() }