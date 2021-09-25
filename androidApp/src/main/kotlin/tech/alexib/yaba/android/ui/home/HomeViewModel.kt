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
package tech.alexib.yaba.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.minus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import tech.alexib.yaba.data.store.HomeScreenAction
import tech.alexib.yaba.data.store.HomeScreenState
import tech.alexib.yaba.data.store.HomeStore
import tech.alexib.yaba.util.stateInDefault

class HomeViewModel : ViewModel(), KoinComponent {

    private val homeStore: HomeStore by inject { parametersOf(Dispatchers.Main) }

    val state = homeStore.state
        .stateInDefault(viewModelScope, HomeScreenState.Empty)

    init {
        homeStore.init()
        Firebase.messaging.isAutoInitEnabled = true
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
    }

    override fun onCleared() {
        super.onCleared()
        homeStore.dispose()
    }

    fun submit(action: HomeScreenAction) {
        homeStore.submit(action)
    }
}
