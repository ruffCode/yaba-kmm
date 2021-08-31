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
package tech.alexib.yaba.android.ui.accounts.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.store.AccountDetailScreenState
import tech.alexib.yaba.data.store.AccountDetailStore
import tech.alexib.yaba.util.stateInDefault

class AccountDetailScreenViewModel : ViewModel(), KoinComponent {
    private val store: AccountDetailStore by inject()

    val state: StateFlow<AccountDetailScreenState> =
        store.state.stateInDefault(viewModelScope, AccountDetailScreenState.Empty)

    fun init(accountId: Uuid, itemId: Uuid) {
        store.init(accountId, itemId)
    }
}

