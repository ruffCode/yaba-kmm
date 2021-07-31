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

import tech.alexib.yaba.data.domain.dto.InstitutionDto

internal fun tech.alexib.yaba.fragment.Institution.toDto(): InstitutionDto = this.run {
    InstitutionDto(
        institutionId = institutionId,
        name = name,
        logo = logo,
        primaryColor = primaryColor ?: "#095aa6"
    )
}

internal fun tech.alexib.yaba.fragment.ItemWithInstitution.Institution.toDto(): InstitutionDto =
    this.run {
        val institution = fragments.institution
        institution.toDto()
    }
