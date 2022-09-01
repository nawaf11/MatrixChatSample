package com.example.matrixchat.ui.chat_app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matrixchat.NavHostPages
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


@Composable
fun MatrixRoomList(navController : NavController, currentSession : Session) {

    // Create query to listen to room summary list
    val roomSummariesQuery = roomSummaryQueryParams {
        memberships = Membership.activeMemberships()
    }

    // Then you can subscribe to livedata..
    val roomState by currentSession.roomService().getRoomSummariesLive(roomSummariesQuery)
        .observeAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "Room List", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)

        roomState?.let { roomState ->
            LazyColumn(content = {
                items(roomState?.size ?: 0) {
                    val roomSummary = roomState[it]
                    val roomDetails = currentSession.roomService().getRoom(roomSummary.roomId)

                    Surface(color = Color.Gray, modifier = Modifier.padding(10.dp).fillMaxWidth()
                        .clickable {
                            navController.navigate(NavHostPages.matrix_room_details + "/${roomSummary.roomId}")
                        }) {

                        Column(Modifier.padding(6.dp)) {

                            Text(
                                text = "Room Name: ${roomSummary.name}",
                                Modifier,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )

                        }

                    }

                }
            })
        }

    }

}