package tech.alexib.yaba.kmm.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.LocalDate
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.data.db.UserTransasction


interface TransactionValues{
    enum class Type {
        DIGITAL,
        PLACE,
        SPECIAL,
        UNRESOLVED
    }
}

enum class TransactionType {
    DIGITAL,
    PLACE,
    SPECIAL,
    UNRESOLVED
}


data class Transaction(
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
    val merchantName:String? = null
)

fun TransactionEntity.toDomain() = Transaction(
    id =id,
    name = name,
    type = type,
    amount = amount,
    date =date,
    accountId =account_id,
    itemId =item_id,
    category = category,
    subcategory = subcategory,
    isoCurrencyCode = iso_currency_code,
    pending = pending
)

fun UserTransasction.toDomain() = Transaction(
    id =id,
    name = name,
    type = type,
    amount = amount,
    date =date,
    accountId =account_id,
    itemId =item_id,
    category = category,
    subcategory = subcategory,
    isoCurrencyCode = iso_currency_code,
    pending = pending
)