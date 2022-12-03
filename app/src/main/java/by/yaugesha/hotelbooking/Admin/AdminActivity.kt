package by.yaugesha.hotelbooking.Admin

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
import by.yaugesha.hotelbooking.Admin.ui.theme.HotelbookingTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments: Bundle? = intent.extras;
        val login: String = arguments?.get("login") as String
        setContent {
            HotelbookingTheme {
                AdminNavigation(login)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    HotelbookingTheme {
    }
}