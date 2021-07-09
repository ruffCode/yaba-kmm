package tech.alexib.yaba.kmm.data.api.dto

import com.benasher44.uuid.Uuid
import tech.alexib.yaba.data.db.ItemEntity
import tech.alexib.yaba.kmm.model.PlaidItem
import tech.alexib.yaba.kmm.model.User

internal data class NewItemData(
    val user: User,
    val item: PlaidItem,
    val accounts: List<AccountWithTransactionsDto>
)

internal data class ItemDto(
    val id: Uuid,
    val plaidInstitutionId: String,
    val userId: Uuid
)

internal fun ItemDto.toEntity(): ItemEntity = ItemEntity(
    id = id,
    plaid_institution_id = plaidInstitutionId,
    user_id = userId
)

internal fun List<ItemDto>.toEntities(): List<ItemEntity> = this.map { it.toEntity() }
