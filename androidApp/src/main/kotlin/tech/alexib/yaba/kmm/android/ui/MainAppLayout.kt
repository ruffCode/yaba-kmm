package tech.alexib.yaba.kmm.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import tech.alexib.yaba.kmm.android.AppNavigation
import tech.alexib.yaba.kmm.android.YabaBottomBar

@Composable
fun MainAppLayout(
    finishActivity: () -> Unit
) {
    val navController = rememberNavController()

    val insetsPadding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.navigationBars
    )
    Scaffold(
        bottomBar = {
            YabaBottomBar(
                navController,
                modifier = Modifier.padding(
                    insetsPadding
                )
            )
        },

    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            AppNavigation(navController = navController, finishActivity)
        }
    }
}
