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
import co.touchlab.kermit.Kermit
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.fcm.PushTokenManager
import tech.alexib.yaba.data.task.UpdateTransactionsWorker

class FCMService : FirebaseMessagingService(), KoinComponent {

    private val log: Kermit by inject { parametersOf("FCMService") }
    private val pushTokenManager: PushTokenManager by inject()
    private val workManager: WorkManager by inject()
    override fun onNewToken(token: String) {
        log.d { "new token" }
        saveToken(token)
    }

    private fun saveToken(token: String) {
        pushTokenManager.saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        log.d { "message received" }
        message.getTransactionUpdatedId()?.let { updateId ->
            val work = OneTimeWorkRequestBuilder<UpdateTransactionsWorker>()
                .setInputData(UpdateTransactionsWorker.addData(updateId)).build()
            workManager.enqueue(work)
        }
    }
}

private fun RemoteMessage.getTransactionUpdatedId(): Uuid? {
    return if (this.data.isNotEmpty()) {
        this.data.updateId()
    } else null
}

private fun Map<String, String>.updateId(): Uuid? = this["updateId"]?.let { uuidFrom(it) }
