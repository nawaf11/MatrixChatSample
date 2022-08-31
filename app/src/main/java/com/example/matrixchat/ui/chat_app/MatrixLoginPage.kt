package com.example.matrixchat.ui.chat_app

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixchat.MyApp
import com.example.matrixchat.matrix.MatrixSessionHolder
import com.example.matrixchat.matrix.MatrixUtils
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController


class MatrixLoginPageViewModel constructor(context : Application) : AndroidViewModel(context) {

    val showErrorMessage : MutableState<String?> = mutableStateOf(null)
    val onLoginSuccess : MutableState<Unit?> = mutableStateOf(null)

    // return errorMsg if fail
    fun loginMatrix(username : String, password : String) {

        val homeServerConnectionConfig = MatrixUtils.homServerConfig()

        if(homeServerConnectionConfig == null) {
            showErrorMessage.value = "homeServerConnectionConfig is NULL !"
            return
        }

        viewModelScope.launch {

            try {
                val session = MyApp.getMatrix(getApplication())
                    .authenticationService().directAuthentication(
                        homeServerConnectionConfig,
                        username,
                        password,
                        "Android-User_$username"
                    )

                MatrixSessionHolder.setSession(session)
                session.open()
                session.syncService().startSync(true)
//                displayRoomList()

                onLoginSuccess.value = Unit

            } catch (failure: Throwable) {
                showErrorMessage.value = "Failure: $failure"
            }
        }

    }

}


@Composable
fun MatrixLoginPage(navController: NavController,
                    viewModel : MatrixLoginPageViewModel = viewModel()) {

    var username by remember { mutableStateOf(TextFieldValue("nawaf11")) }
    var password by remember { mutableStateOf(TextFieldValue("ahmedXv47bC7*")) }
    val context = LocalContext.current

    val showErrorMessage by viewModel.showErrorMessage
    val onLoginSuccess by viewModel.onLoginSuccess

    LaunchedEffect(onLoginSuccess) {
        if(onLoginSuccess == null)
            return@LaunchedEffect

        Toast.makeText(context, "Successfully Logged In", Toast.LENGTH_SHORT).show()
        navController.navigate("matrix_main_page")
    }

    LaunchedEffect(showErrorMessage) {
        if(showErrorMessage != null)
            Toast.makeText(context, showErrorMessage, Toast.LENGTH_SHORT).show()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {

        Text("Login Page")

        TextField(modifier = Modifier.padding(top = 20.dp),
            value = username,
            onValueChange = { newText ->
                username = newText
            }
        )

        TextField(modifier = Modifier.padding(top = 20.dp),
            value = password,
            onValueChange = { newText ->
                password = newText
            },
            visualTransformation = PasswordVisualTransformation(),
        )

        Button(modifier = Modifier, onClick = {
            viewModel.loginMatrix(username.text, password.text)
        }) {
            Text(text = "Matrix Login")
        }
    }

}
