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
package tech.alexib.yaba.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom

interface PlaidItemBase {
    val id: Uuid
    val plaidInstitutionId: String
    val name: String
    val base64Logo: String
}

data class PlaidItem(
    override val id: Uuid,
    override val plaidInstitutionId: String,
    override val name: String,
    override val base64Logo: String,
) : PlaidItemBase

data class PlaidItemWithAccounts(
    val plaidItem: PlaidItem,
    val accounts: List<Account>,
) : PlaidItemBase by plaidItem {
    val hiddenCount: Int by lazy {
        accounts.filter { it.hidden }.size
    }
}

object PlaidItemStubs {
    @Suppress("MaxLineLength")
    val TDBank = PlaidItem(
        id = uuidFrom("1a1222da-4b2a-45d1-8dbd-c907c083f111"),
        plaidInstitutionId = "ins_14",
        name = "TD Bank",
        base64Logo = "iVBORw0KGgoAAAANSUhEUgAAAJgAAACYCAMAAAAvHNATAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAJcEhZcwAACxMAAAsTAQCanBgAAAA8UExURUdwTO3t7e7u7uzs7O3t7e3t7e3t7e/v7+zs7O7u7u3t7REREZubm0hISH9/fy8vL9nZ2bq6uh8fH2dnZxAlB0cAAAAKdFJOUwB4X5DFoe8ZQd81kq9CAAADm0lEQVR42u2c2a7qMAxFSZuZAi38/79eaJmHUjtOstE9+wmJSizZjmNSx6tVopxvWqN1CHFUCFobpbxfVZRXRtv4SUc+XwVqhukqq4vCOd8ugLoomEJsJKqz4fKzOUWmOtutcTmNZZhYo0wuNK9jorSHxBrRpK3mRLDEHepUFJQSQ/Mhiio0MuYyUVwS/vQ2ZlCy0VwbM6lNMpoLMZtCAlljY0ZZtjtVzCwFsxpfVmfVXC+7ReUM+5QlUIqLSlaOi0ZWkotCVpaLQKZjYS1cmyYWl4HI98w9oIlV9HXfdLYOmHVYC3Lx0mxjNbWAATbJIzryS5iZWFUG0pEzzqzryJmVqWJ1vd0AXASQw4v8c50BarB38a8xwF5M5mPENJlGAdOQEfZqMoMDZkAN9riXN0BgD+lfZJfsDptOxGSyuaLbrY/adaLhb4SwhNBuRXbqP6Nhs75TMto1/L0klgSal/DkDWu/lXJom74m77F6sVizqZ58xjppK4Hmk7LrO6yjegE0lVBYfMB6QtsMCSWGlcWSQLPMEPuCJYDmOf/aFmAlozX0LNYvw5rQrin30NOLMmLsH5ZijWiXp9dbevQTjbxZjnWPtiFHPzX2O6oBJn9SM5qjL8rRZPvFMdPvxxijRz95UU6/tFv6+FQ/9gXA4jBtN5tF2rEceSowGDXPYU3Ugf4bhrNT9jsa147+E8d8wdnChz2Fa9+zwFhVIsVmvAIjMMvX7j6kxwRy+eY+m3a8wJ/AIiZY/AP7A/t/wWDTBSyYxgTTuGAGE8wwX89nB1PMI5UCYB4TzDPfPRQA450MZwfjHo/lBtPco+HcYIbbPJAbTJEP7oZu1PjX8jB9nk6qz5+76ez19amBGvvE6N+umSKdQ1n64XAZME3vUCkDpugvIMqAOfrLt6FjihL8oX7/3+csBtUM8pAsKraYzicLQF8avMaeR08KvBQXVcDqArypAeiwnsuu2E1HSOHvfqOxDbcVELZ5EsVkboXZb2p+qaUZtgkct22+fvw3v3Y1o/JePnczyUM6EvnCFO4VM9hLebjXGHEvflbJZi3QZf/5IuzHrmPDXmDHvfKPOyQBd6wE7iCOMvmMOVgIdNgL8Hgc3IFCuCOYTu7MYjQrMFQOdcxXBqMFsRl8qKPkRP0JOhcQdJKizjWyM8mheSeJOu4KtSrnEFHuJFHblhoJawKJyq3KCXK07w3O6BmmOsOQb3Reqefx0W2T7r1/3Z/PsFrHmzcAAAAASUVORK5CYII="
    )
//    val PNCBank = PlaidItem(
//        id = PlaidItemId(uuidFrom("6e2bc39f-6bde-44f0-a547-3dcb54932e6a")),
//        plaidInstitutionId = "ins_13",
//        name = "PNC",
//        base64Logo = "iVBORw0KGgoAAAANSUhEUgAAAJgAAACYCAMAAAAvHNATAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAJcEhZcwAACxMAAAsTAQCanBgAAAA8UExURUdwTO3t7e7u7uzs7O3t7e3t7e3t7e/v7+zs7O7u7u3t7REREZubm0hISH9/fy8vL9nZ2bq6uh8fH2dnZxAlB0cAAAAKdFJOUwB4X5DFoe8ZQd81kq9CAAADm0lEQVR42u2c2a7qMAxFSZuZAi38/79eaJmHUjtOstE9+wmJSizZjmNSx6tVopxvWqN1CHFUCFobpbxfVZRXRtv4SUc+XwVqhukqq4vCOd8ugLoomEJsJKqz4fKzOUWmOtutcTmNZZhYo0wuNK9jorSHxBrRpK3mRLDEHepUFJQSQ/Mhiio0MuYyUVwS/vQ2ZlCy0VwbM6lNMpoLMZtCAlljY0ZZtjtVzCwFsxpfVmfVXC+7ReUM+5QlUIqLSlaOi0ZWkotCVpaLQKZjYS1cmyYWl4HI98w9oIlV9HXfdLYOmHVYC3Lx0mxjNbWAATbJIzryS5iZWFUG0pEzzqzryJmVqWJ1vd0AXASQw4v8c50BarB38a8xwF5M5mPENJlGAdOQEfZqMoMDZkAN9riXN0BgD+lfZJfsDptOxGSyuaLbrY/adaLhb4SwhNBuRXbqP6Nhs75TMto1/L0klgSal/DkDWu/lXJom74m77F6sVizqZ58xjppK4Hmk7LrO6yjegE0lVBYfMB6QtsMCSWGlcWSQLPMEPuCJYDmOf/aFmAlozX0LNYvw5rQrin30NOLMmLsH5ZijWiXp9dbevQTjbxZjnWPtiFHPzX2O6oBJn9SM5qjL8rRZPvFMdPvxxijRz95UU6/tFv6+FQ/9gXA4jBtN5tF2rEceSowGDXPYU3Ugf4bhrNT9jsa147+E8d8wdnChz2Fa9+zwFhVIsVmvAIjMMvX7j6kxwRy+eY+m3a8wJ/AIiZY/AP7A/t/wWDTBSyYxgTTuGAGE8wwX89nB1PMI5UCYB4TzDPfPRQA450MZwfjHo/lBtPco+HcYIbbPJAbTJEP7oZu1PjX8jB9nk6qz5+76ez19amBGvvE6N+umSKdQ1n64XAZME3vUCkDpugvIMqAOfrLt6FjihL8oX7/3+csBtUM8pAsKraYzicLQF8avMaeR08KvBQXVcDqArypAeiwnsuu2E1HSOHvfqOxDbcVELZ5EsVkboXZb2p+qaUZtgkct22+fvw3v3Y1o/JePnczyUM6EvnCFO4VM9hLebjXGHEvflbJZi3QZf/5IuzHrmPDXmDHvfKPOyQBd6wE7iCOMvmMOVgIdNgL8Hgc3IFCuCOYTu7MYjQrMFQOdcxXBqMFsRl8qKPkRP0JOhcQdJKizjWyM8mheSeJOu4KtSrnEFHuJFHblhoJawKJyq3KCXK07w3O6BmmOsOQb3Reqefx0W2T7r1/3Z/PsFrHmzcAAAAASUVORK5CYII="
//    )

    val items = listOf(TDBank)

    val itemWithAccounts =
        PlaidItemWithAccounts(accounts = AccountStubs.accounts, plaidItem = TDBank)
}
