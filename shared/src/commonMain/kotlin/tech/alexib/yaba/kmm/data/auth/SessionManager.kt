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
package tech.alexib.yaba.kmm.data.auth

import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import tech.alexib.yaba.kmm.data.repository.AuthResult

interface SessionManager {
    fun isLoggedIn(): Flow<Boolean>
    suspend fun setToken(token: String)
    suspend fun logout()
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun startLogoutTimer()
    fun isShowOnBoarding(): Flow<Boolean>
    suspend fun setUserId(userId: Uuid)
}
