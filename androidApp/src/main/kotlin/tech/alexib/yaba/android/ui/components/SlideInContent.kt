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
package tech.alexib.yaba.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

@Composable
fun SlideInContent(visible: Boolean, content: @Composable () -> Unit) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = 500,
                easing = FastOutLinearInEasing
            ),
            initialAlpha = 0.3f
        )
    }
    val enterSlideIn = remember {
        slideInVertically(
            initialOffsetY = { -40 },
            animationSpec = TweenSpec(
                durationMillis = 500,
                easing = FastOutLinearInEasing
            ),
        )
    }
    val enterExpand = remember {
        expandVertically(
            animationSpec = tween(200),
            expandFrom = Alignment.Top
        )
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = 200,
                easing = LinearOutSlowInEasing
            )
        )
    }
    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(200))
    }
    val exitSlideOut = remember {
        slideOutVertically(
            animationSpec = TweenSpec(
                durationMillis = 200,
                easing = LinearOutSlowInEasing
            )
        )
    }
    AnimatedVisibility(
        visible = visible,
        enter = enterSlideIn + enterExpand + enterFadeIn,
        exit = exitSlideOut + exitCollapse + exitFadeOut
    ) {
        content()
    }
}
