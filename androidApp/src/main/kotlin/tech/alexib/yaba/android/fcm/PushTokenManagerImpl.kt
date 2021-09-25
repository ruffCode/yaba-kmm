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
package tech.alexib.yaba.android.fcm

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.data.fcm.PushTokenManager
import tech.alexib.yaba.data.repository.PushTokenRepository
import tech.alexib.yaba.data.task.UserPushTokenWorker

class PushTokenManagerImpl : PushTokenManager, KoinComponent {
    private val workManager: WorkManager by inject()
    private val pushTokenRepository: PushTokenRepository by inject()

    override fun saveToken(token: String) {
        val work = OneTimeWorkRequestBuilder<UserPushTokenWorker>()
            .setInputData(UserPushTokenWorker.addToken(token)).build()
        workManager.enqueue(work)
    }

    override fun deleteToken(token: String) {
        pushTokenRepository.delete(token)
    }
}
