package by.yaugesha.hotelbooking.Main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import by.yaugesha.hotelbooking.Main.MainNavigation
import by.yaugesha.hotelbooking.ui.theme.HotelbookingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelbookingTheme {
                MainNavigation(login = "12")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HotelbookingTheme {
    }
}
