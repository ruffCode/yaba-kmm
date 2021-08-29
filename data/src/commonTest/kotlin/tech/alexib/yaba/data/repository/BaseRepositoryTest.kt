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
package tech.alexib.yaba.data.repository

import com.benasher44.uuid.Uuid
import tech.alexib.yaba.data.TestDependencies
import tech.alexib.yaba.data.domain.dto.UserDataDto
import tech.alexib.yaba.data.domain.stubs.UserDataDtoStubs

internal open class BaseRepositoryTest {
    //    val kermit = Kermit(CommonLogger())
//    val backgroundDispatcher = Dispatchers.Unconfined
//    val settings = MockSettings().toFlowSettings(backgroundDispatcher)
//    val authSettings = AuthSettings.Impl(settings)
//    val userIdProvider: UserIdProvider by lazy {
//        UserIdProvider.Impl(
//            authSettings, backgroundDispatcher,
//            kermit.withTag("UserIdProvider")
//        )
//    }
//
//
//    private val driver: SqlDriver = createInMemorySqlDriver()
//    val database: YabaDatabase by lazy {
//        YabaDatabase(driver, kermit.withTag("YabaDatabase"))
//    }
//    val yabaDb: YabaDb by lazy {
//        database.getInstance()
//    }
//
//    val dao = TestDaos(backgroundDispatcher, yabaDb)
//
// //    val userDao: UserDao by lazy {
// //        UserDao.Impl(yabaDb, backgroundDispatcher)
// //    }
//    val userRepository: UserRepository by lazy {
//        UserRepositoryImpl(userIdProvider, kermit.withTag("UserRepository"), dao.userDao)
//    }
    val deps = TestDependencies

    val user = UserDataDtoStubs.user
    val userId = user.id

    suspend fun setupTest() {
        deps.authRepository.login(
            UserDataDtoStubs.validLogin.email,
            UserDataDtoStubs.validLogin.password
        )
        deps.userDao.insert(user)
    }

    suspend fun login() {
        deps.authRepository.login(
            UserDataDtoStubs.validLogin.email,
            UserDataDtoStubs.validLogin.password
        )
    }

    suspend fun cleanup() {
        deps.userDao.deleteById(userId)
        deps.authRepository.logout()
        deps.authSettings.clearAppSettings()
    }

    suspend fun setUserId(id: Uuid = userId) {
        deps.authSettings.setUserId(id)
    }

    suspend fun insertAllUserData(userData: UserDataDto = UserDataDtoStubs.userData) {
        deps.userDao.insertUserData(userData)
    }
}
