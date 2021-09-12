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
package tech.alexib.yaba.data.stubs

import kotlinx.serialization.decodeFromString
import tech.alexib.yaba.data.domain.dto.ItemDto
import tech.alexib.yaba.data.stubs.json.plaidItemChaseJson
import tech.alexib.yaba.data.stubs.json.plaidItemWellsFargoJson
import tech.alexib.yaba.util.jSerializer

object PlaidItemDtoStubs {
    val chase: ItemDto by lazy { jSerializer.decodeFromString(plaidItemChaseJson) }
    val wellsFargo: ItemDto by lazy { jSerializer.decodeFromString(plaidItemWellsFargoJson) }
}
