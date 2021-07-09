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
