package com.example.matrixchat.ui.chat_app

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matrixchat.matrix.MatrixSessionHolder


class MatrixMainPageViewModel constructor(context : Application) : AndroidViewModel(context) {

}



@Composable
fun MatrixMainPage(navController: NavController, viewModel : MatrixMainPageViewModel = viewModel()) {


    val currentSession = MatrixSessionHolder.currentSession

    if(currentSession == null) {
        Text(text = "currentSession is Null !")
        return
    }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(" Hello " + currentSession.myUserId)

        MatrixRoomList(navController, currentSession)

    }

}
