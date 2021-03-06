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
package tech.alexib.yaba.data.db

import co.touchlab.kermit.Kermit
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import tech.alexib.yaba.data.db.util.DateAdapter
import tech.alexib.yaba.data.db.util.UuidAdapter

class YabaDatabase(
    private val driver: SqlDriver,
    private val log: Kermit
) {
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
