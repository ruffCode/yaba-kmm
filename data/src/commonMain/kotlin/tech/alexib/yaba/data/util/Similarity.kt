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
package tech.alexib.yaba.data.util

import kotlin.math.max
import kotlin.math.min

object Similarity {
// Credit to AJ Alt
// https://github.com/ajalt/clikt/blob/master/clikt/src/commonMain/kotlin/com/github/ajalt/clikt/core/JaroWinkerSimilarity.kt
    private fun jaroSimilarity(s1: String, s2: String): Double {
        if (s1.isEmpty() && s2.isEmpty()) return 1.0
        else if (s1.isEmpty() || s2.isEmpty()) return 0.0
        else if (s1.length == 1 && s2.length == 1) return if (s1[0] == s2[0]) 1.0 else 0.0

        val searchRange: Int = max(s1.length, s2.length) / 2 - 1
        val s2Consumed = BooleanArray(s2.length)
        var matches = 0.0
        var transpositions = 0
        var s2MatchIndex = 0

        for ((i, c1) in s1.withIndex()) {
            val start = max(0, i - searchRange)
            val end = min(s2.lastIndex, i + searchRange)
            for (j in start..end) {
                val c2 = s2[j]
                if (c1 != c2 || s2Consumed[j]) continue
                s2Consumed[j] = true
                matches += 1
                if (j < s2MatchIndex) transpositions += 1
                s2MatchIndex = j
                break
            }
        }

        return when (matches) {
            0.0 -> 0.0
            else -> (
                matches / s1.length +
                    matches / s2.length +
                    (matches - transpositions) / matches
                ) / 3.0
        }
    }

    fun jaroWinklerSimilarity(s1: String, s2: String): Double {
        val prefix = s1.commonPrefixWith(s2)
        val prefixLength = prefix.length
        val jaro = jaroSimilarity(s1, s2)
        val winkler = jaro + (0.1 * prefixLength * (1 - jaro))
        return min(winkler, 1.0)
    }
}
