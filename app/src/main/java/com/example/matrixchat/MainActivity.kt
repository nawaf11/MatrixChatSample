package com.example.matrixchat

import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.matrixchat.matrix.MatrixSessionHolder
import com.example.matrixchat.ui.chat_app.MatrixLoginPage
import com.example.matrixchat.ui.chat_app.MatrixMainPage
import com.example.matrixchat.ui.chat_app.MatrixRoomDetails
import com.example.matrixchat.ui.theme.MatrixChatTheme

object NavHostPages {
    const val matrix_login_page = "matrix_login_page"
    const val matrix_main_page = "matrix_main_page"
    const val matrix_room_details = "matrix_room_details"

}

class MainActivity : ComponentActivity() {

    @Composable
    fun HtmlText(html: String, modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context -> TextView(context) },
            update = { it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()
            val context = LocalContext.current

            MatrixChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val startDestination : String = if(MatrixSessionHolder.currentSession == null) NavHostPages.matrix_login_page else NavHostPages.matrix_main_page

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(NavHostPages.matrix_login_page) { MatrixLoginPage(navController) }
                        composable(NavHostPages.matrix_main_page) { MatrixMainPage(navController) }
                        composable(NavHostPages.matrix_room_details + "/{roomId}",
                            arguments = listOf(navArgument("roomId") { type = NavType.StringType })) {
                            val roomId = it.arguments?.getString("roomId")
                            if(roomId != null && MatrixSessionHolder.currentSession != null)
                                MatrixRoomDetails(navController, MatrixSessionHolder.currentSession!!, roomId)
                        }


                        /*...*/
                    }

                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MatrixChatTheme {
    }
}