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
package tech.alexib.yaba.android.util

import android.app.NotificationManager

sealed class YabaNotificationChannel {
    abstract val name: String
    abstract val id: String
}

object NewTransactionChannel : YabaNotificationChannel() {
    override val id: String = "new_transaction"
    override val name: String = "New transactions"
}

fun NotificationManager.isChannelActive(channel: YabaNotificationChannel): Boolean {
    val notificationChannel = this.getNotificationChannel(channel.id)
    return notificationChannel != null && notificationChannel.importance != NotificationManager.IMPORTANCE_NONE
}
