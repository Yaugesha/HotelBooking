package by.yaugesha.hotelbooking.Authorization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import by.yaugesha.hotelbooking.Authorization.ui.theme.HotelbookingTheme

class AuthorizationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelbookingTheme {
                AuthNavigation()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    HotelbookingTheme {
    }
}