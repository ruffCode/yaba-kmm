package tech.alexib.yaba.kmm.data.db

import org.koin.dsl.module
import tech.alexib.yaba.data.db.YabaDb

internal val dbModule = module {

    single { YabaDatabase(get()).getInstance() }
}