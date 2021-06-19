package tech.alexib.yaba.kmm.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import tech.alexib.yaba.kmm.Greeting
import tech.alexib.yaba.kmm.android.ui.MainAppLayout
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            YabaTheme {
                MainAppLayout{
                    finish()
                }
            }

        }
    }
}
