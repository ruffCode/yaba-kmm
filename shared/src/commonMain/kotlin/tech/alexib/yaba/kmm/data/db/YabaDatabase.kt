package tech.alexib.yaba.kmm.data.db

import co.touchlab.kermit.Kermit
import co.touchlab.stately.ensureNeverFrozen
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
):KoinComponent {
    private val log: Kermit by inject { parametersOf("YabaDatabase") }
    fun getInstance(): YabaDb {
        return createQueryWrapper(driver)
    }

    private fun createQueryWrapper(driver: SqlDriver): YabaDb {
        log.d { "schema version ${YabaDb.Schema.version}" }
        return YabaDb(
            driver = driver,
            AccountEntityAdapter = AccountEntity.Adapter(
                item_idAdapter = UUIDAdapter(),
                idAdapter = UUIDAdapter(),
                typeAdapter = EnumColumnAdapter(),
                subtypeAdapter = EnumColumnAdapter()
            ),
            ItemEntityAdapter = ItemEntity.Adapter(
                idAdapter = UUIDAdapter(),
                user_idAdapter = UUIDAdapter()
            ),
            TransactionEntityAdapter = TransactionEntity.Adapter(
                idAdapter = UUIDAdapter(),
                item_idAdapter = UUIDAdapter(),
                dateAdapter = DateAdapter(),
                account_idAdapter = UUIDAdapter(),
                typeAdapter = EnumColumnAdapter()
            ),
            UserEntityAdapter = UserEntity.Adapter(
                idAdapter = UUIDAdapter()
            )
        )
    }


}