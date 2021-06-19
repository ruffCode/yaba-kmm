package tech.alexib.yaba.kmm.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import tech.alexib.yaba.kmm.android.AppNavigation

@Composable
fun MainAppLayout(
finishActivity:() -> Unit
) {
    val navController = rememberNavController()


    Scaffold {
        Box(modifier = Modifier.fillMaxSize()) {
            AppNavigation(navController = navController,finishActivity)
        }
    }
}