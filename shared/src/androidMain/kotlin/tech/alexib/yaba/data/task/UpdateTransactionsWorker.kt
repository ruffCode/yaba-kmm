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
package tech.alexib.yaba.data.task

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import tech.alexib.yaba.data.repository.TransactionRepository

class UpdateTransactionsWorker(
    context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters), KoinComponent {

    private val transactionRepository: TransactionRepository by inject()

    companion object {
        const val key = "request"
        fun addData(updateId: Uuid) = Data.Builder()
            .putString(key, updateId.toString()).build()
    }

    override suspend fun doWork(): Result {
        val requestString = inputData.getString(key)

        requestString?.let {
            val updateId = uuidFrom(it)
            delay(2000)
            transactionRepository.updateTransactions(updateId)
        }
        return Result.success()
    }
}
