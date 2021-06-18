package tech.alexib.yaba.kmm.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import tech.alexib.yaba.kmm.Greeting
import tech.alexib.yaba.kmm.android.ui.theme.YabaTheme
import tech.alexib.yaba.kmm.data.repository.AuthRepository

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val repository:AuthRepository by inject()

        lifecycleScope.launch {
            repository.login("alexi2@aol.com","password").getOrThrow()
        }
        setContent {
            YabaTheme {
                Text(text = greet())
            }
        }
    }
}
