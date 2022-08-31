package com.example.matrixchat.ui.chat_app

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matrixchat.matrix.MatrixSessionHolder
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class MatrixMainPageViewModel constructor(context : Application) : AndroidViewModel(context) {

}



@Composable
fun MatrixMainPage(viewModel : MatrixMainPageViewModel = viewModel()) {


    val currentSession = MatrixSessionHolder.currentSession

    if(currentSession == null) {
        Text(text = "currentSession is Null !")
        return
    }

    // Create query to listen to room summary list
    val roomSummariesQuery = roomSummaryQueryParams {
        memberships = Membership.activeMemberships()
    }

    // Then you can subscribe to livedata..
    val roomState by currentSession.roomService().getRoomSummariesLive(roomSummariesQuery).observeAsState()

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(" Hello " + currentSession.myUserId)

        Text(text = "Room List ${roomState?.size}, firstItem name ==>  ${roomState?.firstOrNull()?.name}")


    }

}
