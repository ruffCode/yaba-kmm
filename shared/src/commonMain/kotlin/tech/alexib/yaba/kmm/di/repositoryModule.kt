package tech.alexib.yaba.kmm.di

import org.koin.dsl.module
import tech.alexib.yaba.kmm.data.Initializer
import tech.alexib.yaba.kmm.data.InitializerImpl
import tech.alexib.yaba.kmm.data.repository.AccountRepository
import tech.alexib.yaba.kmm.data.repository.AccountRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.AuthApiRepository
import tech.alexib.yaba.kmm.data.repository.AuthApiRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.TransactionRepository
import tech.alexib.yaba.kmm.data.repository.TransactionRepositoryImpl
import tech.alexib.yaba.kmm.data.repository.UserIdProvider

val repositoryModule = module {
    single<AuthApiRepository> { AuthApiRepositoryImpl(get(), getWith("AuthRepository")) }
    single<Initializer> { InitializerImpl() }
    single<AccountRepository> { AccountRepositoryImpl() }
    single<TransactionRepository> { TransactionRepositoryImpl() }
    single { UserIdProvider() }
}
