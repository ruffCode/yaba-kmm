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
package tech.alexib.yaba.kmm.data.db

import co.touchlab.kermit.Kermit
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.db.AccountEntity
import tech.alexib.yaba.data.db.ItemEntity
import tech.alexib.yaba.data.db.TransactionEntity
import tech.alexib.yaba.data.db.UserEntity
import tech.alexib.yaba.data.db.YabaDb

class YabaDatabase(
    private val driver: SqlDriver
) : KoinComponent {
    private val log: Kermit by inject { parametersOf("YabaDatabase") }
    fun getInstance(): YabaDb {
        return createQueryWrapper(driver)
    }

    private fun createQueryWrapper(driver: SqlDriver): YabaDb {
        log.d { "schema version ${YabaDb.Schema.version}" }
        return YabaDb(
            driver = driver,
            AccountEntityAdapter = AccountEntity.Adapter(
                item_idAdapter = UuidAdapter(),
                idAdapter = UuidAdapter(),
                typeAdapter = EnumColumnAdapter(),
                subtypeAdapter = EnumColumnAdapter()
            ),
            ItemEntityAdapter = ItemEntity.Adapter(
                idAdapter = UuidAdapter(),
                user_idAdapter = UuidAdapter()
            ),
            TransactionEntityAdapter = TransactionEntity.Adapter(
                idAdapter = UuidAdapter(),
                item_idAdapter = UuidAdapter(),
                dateAdapter = DateAdapter(),
                account_idAdapter = UuidAdapter(),
                typeAdapter = EnumColumnAdapter()
            ),
            UserEntityAdapter = UserEntity.Adapter(
                idAdapter = UuidAdapter()
            )
        )
    }
}
