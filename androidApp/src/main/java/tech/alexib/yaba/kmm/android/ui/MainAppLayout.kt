package tech.alexib.yaba.kmm.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsHeight
import tech.alexib.yaba.kmm.android.AppNavigation
import tech.alexib.yaba.kmm.android.YabaBottomBar

@Composable
fun MainAppLayout(
finishActivity:() -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            YabaBottomBar(navController)
            Spacer(Modifier.navigationBarsHeight().fillMaxWidth())
        }
    ) {contentPadding->
        Box(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
            AppNavigation(navController = navController,finishActivity)
        }
    }
}