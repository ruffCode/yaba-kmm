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
package tech.alexib.yaba.data.db.mapper

import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.data.domain.dto.AccountDto

internal fun AccountDto.toEntity(): AccountEntity = AccountEntity(
    id = id,
    name = name,
    current_balance = currentBalance,
    available_balance = availableBalance,
    credit_limit = creditLimit,
    mask = mask,
    item_id = itemId,
    type = type,
    subtype = subtype,
    hidden = hidden
)

internal fun List<AccountDto>.toEntities(): List<AccountEntity> = this.map { it.toEntity() }
