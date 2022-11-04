package ro.ase.dam.yeapauctions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ro.ase.dam.yeapauctions.ui.theme.YeapAuctionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YeapAuctionsTheme {
                   YeapAuctionsApp()
            }
        }
    }
}

