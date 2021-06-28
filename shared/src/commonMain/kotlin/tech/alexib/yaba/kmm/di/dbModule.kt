package tech.alexib.yaba.kmm.di

import org.koin.dsl.module
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.kmm.data.db.YabaDatabase
import tech.alexib.yaba.kmm.data.db.dao.AccountDao
import tech.alexib.yaba.kmm.data.db.dao.AccountDaoImpl
import tech.alexib.yaba.kmm.data.db.dao.InstitutionDao
import tech.alexib.yaba.kmm.data.db.dao.InstitutionDaoImpl
import tech.alexib.yaba.kmm.data.db.dao.ItemDao
import tech.alexib.yaba.kmm.data.db.dao.ItemDaoImpl
import tech.alexib.yaba.kmm.data.db.dao.TransactionDao
import tech.alexib.yaba.kmm.data.db.dao.TransactionDaoImpl
import tech.alexib.yaba.kmm.data.db.dao.UserDao
import tech.alexib.yaba.kmm.data.db.dao.UserDaoImpl

internal val dbModule = module {
    single<YabaDb> { YabaDatabase(get()).getInstance() }
    single<ItemDao> { ItemDaoImpl(get(), get()) }
    single<InstitutionDao> { InstitutionDaoImpl(get(), get()) }
    single<AccountDao> { AccountDaoImpl(get(), get()) }
    single<TransactionDao> { TransactionDaoImpl(get(), get()) }
    single<UserDao> { UserDaoImpl(get(), get()) }
}