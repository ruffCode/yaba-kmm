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
package tech.alexib.yaba.data.db.di

import org.koin.dsl.module
import tech.alexib.yaba.data.db.YabaDatabase
import tech.alexib.yaba.data.db.YabaDb
import tech.alexib.yaba.data.db.dao.AccountDao
import tech.alexib.yaba.data.db.dao.InstitutionDao
import tech.alexib.yaba.data.db.dao.ItemDao
import tech.alexib.yaba.data.db.dao.TransactionDao
import tech.alexib.yaba.data.db.dao.UserDao
import tech.alexib.yaba.di.CoreDependencies.ioDispatcherQualifier
import tech.alexib.yaba.util.getWith

val dbModule = module {
    single<YabaDb> { YabaDatabase(get(), getWith("YabaDb")).getInstance() }
    single<AccountDao> { AccountDao.Impl(get(), get(ioDispatcherQualifier)) }
    single<InstitutionDao> { InstitutionDao.Impl(get(), get(ioDispatcherQualifier)) }
    single<ItemDao> { ItemDao.Impl(get(), get(ioDispatcherQualifier)) }
    single<TransactionDao> { TransactionDao.Impl(get(), get(ioDispatcherQualifier)) }
    single<UserDao> { UserDao.Impl(get(), get(ioDispatcherQualifier)) }
}
