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
package tech.alexib.yaba.data.network.mapper

import com.benasher44.uuid.Uuid
import tech.alexib.yaba.data.domain.dto.ItemDto
import tech.alexib.yaba.fragment.ItemWithInstitution
import tech.alexib.yaba.model.PlaidItem

internal fun ItemWithInstitution.toDto(userId: Uuid) = this.run {
    ItemDto(
        id = id as Uuid,
        plaidInstitutionId = plaidInstitutionId,
        userId
    )
}
internal fun ItemWithInstitution.toPlaidItem() = this.run {
    val institution = institution.fragments.institution
    PlaidItem(
        id = id as Uuid,
        plaidInstitutionId = plaidInstitutionId,
        name = institution.name,
        base64Logo = institution.logo
    )
}
