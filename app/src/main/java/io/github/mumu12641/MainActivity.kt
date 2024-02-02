package io.github.mumu12641

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.mumu12641.ui.page.home.HomeViewModel
import io.github.mumu12641.ui.page.home.MainScreen
import io.github.mumu12641.ui.theme.HeartTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                HeartTheme {
                    MainScreen(homeViewModel = homeViewModel)
                }
            }
        }
    }

}
