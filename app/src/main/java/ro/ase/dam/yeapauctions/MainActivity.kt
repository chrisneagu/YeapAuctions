package ro.ase.dam.yeapauctions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ro.ase.dam.yeapauctions.ui.LoginViewModel
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: LoginViewModel by viewModels()

        setContent {
            YeapAuctionsTheme {
                val windowSize = calculateWindowSizeClass(this)

                YeapAuctionsApp(
                    windowSize = windowSize.widthSizeClass,
                    viewModel = viewModel
                )
            }
        }
    }
}