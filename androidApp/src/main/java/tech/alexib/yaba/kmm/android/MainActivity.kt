package tech.alexib.yaba.kmm.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import tech.alexib.yaba.kmm.android.ui.MainAppLayout
import tech.alexib.yaba.kmm.android.ui.theme.BlueSlate
import tech.alexib.yaba.kmm.android.ui.theme.SystemUiController
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.auth.activityForBio


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        activityForBio = this
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            ProvideWindowInsets(consumeWindowInsets = false) {
                val systemUiController = remember { SystemUiController(window) }
                systemUiController.setSystemBarsColor(BlueSlate)
                YabaTheme {
                    MainAppLayout {
                        finish()
                    }
                }
            }
        }
    }
}
