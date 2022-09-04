package com.example.matrixchat.ui.chat_app

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matrixchat.NavHostPages
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.query.QueryStateEventValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.isTextMessage
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getStateEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.timeline.*
import org.matrix.android.sdk.api.util.toMatrixItem


class MatrixRoomDetailsViewModel constructor(context: Application) : AndroidViewModel(context) {

    private val timelineListener : Timeline.Listener? = null

    val messagesList = mutableStateOf<List<String>>(mutableListOf())

    fun setupRoomListener(currentSession : Session, room : Room) {
        if(timelineListener != null)
            return // Already setup

        room.timelineService().createTimeline(null, TimelineSettings(initialSize = 30))?.also {
            // Don't forget to add listener and start the timeline so it start listening to changes
            it.addListener(object : Timeline.Listener {
                override fun onNewTimelineEvents(eventIds: List<String>) {
                    super.onNewTimelineEvents(eventIds)
                    Log.d("TimeLineListener", "onNewTimelineEvents  ${eventIds.size}")
                }

                override fun onStateUpdated(
                    direction: Timeline.Direction,
                    state: Timeline.PaginationState
                ) {
                    super.onStateUpdated(direction, state)
                    Log.d("TimeLineListener", "onStateUpdated  ${state.hasMoreToLoad} , ${state.inError} , ${state.loading}")
                }

                override fun onTimelineFailure(throwable: Throwable) {
                    super.onTimelineFailure(throwable)
                    Log.d("TimeLineListener", "onTimelineFailure  ${throwable.toString()}")
                }

                override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
                    super.onTimelineUpdated(snapshot)
                    Log.d("TimeLineListener", "onTimelineUpdated Called ${snapshot.size}")

                    val textEventList = mutableListOf<String>()
                    viewModelScope.launch {
                        try {
                        snapshot.forEachIndexed { i, item ->
                            val event = currentSession.eventService()
                                .getEvent(room.roomId, snapshot[i].eventId)
                            Log.d("TimeLineListener", "onTimelineUpdated  type of $i ==> ${event.type}, isTextMessage: ${event.isTextMessage()} , content.size ${event.content?.size}")

                            if (event.isTextMessage()) {
                                Log.d("TimeLineListener", "TextMessage Item, $i")
//                                textEventList.add("TextMessage Item, $i,  Sender ==> ${event.sendState.name}")
                                val c = event.senderId?.let {
                                    currentSession.roomService().getRoomMember(it, item.roomId)
                                }?.toMatrixItem()
                                textEventList.add(" ${c?.displayName + ":"}${item.getLastMessageContent()?.body}")

                            }
                        }
                        }catch (failure: Throwable) {
                            Log.d("error w",failure.message.toString())
                                return@launch
                            }

                        messagesList.value = textEventList
                        Log.d("TimeLineListener", "messagesList ==> ${textEventList.size}")

                    }
                }
            })
            it.start()
        }
    }

}

@Composable
fun MatrixRoomDetails(
    navController: NavController,
    currentSession: Session,
    roomId: String,
    viewModel: MatrixRoomDetailsViewModel = viewModel()
) {
    val value = remember {
        mutableStateOf("")
    }

    val roomDetails = currentSession.roomService().getRoom(roomId)
    val messageListData by viewModel.messagesList
    val context= rememberCoroutineScope()
    roomDetails?.let {
        viewModel.setupRoomListener(currentSession, roomDetails) // Timeline Listner

        val numberOfJoinedMembers = roomDetails.membershipService()?.getNumberOfJoinedMembers()

        Surface(modifier = Modifier.fillMaxWidth()) {

            Column {
                Text(
                    text = "Room-Details:\n\nnumberOfJoinedMembers == > $numberOfJoinedMembers",
                    Modifier.padding(6.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )


                LazyColumn() {

                    items(count = messageListData.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(10.dp)
                                .background(Color.Gray)
                        ) {

                            Box(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = messageListData[index],
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }

                    }
                    item {
                        TextField(modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .fillMaxWidth(),
                            value = value.value,
                            onValueChange = {
                                value.value = it
                            })
                        Button(onClick = {
                            if(value.value.isNotEmpty()) {
                                roomDetails.sendService().sendTextMessage(
                                    text = value.value,
                                    msgType = MessageType.MSGTYPE_TEXT
                                )
                            }
                        }) {
                            Text("Send")
                        }
                        Button(onClick = {
                            context.launch {
                                try {
                                    currentSession.signOutService().signOut(false)
                                    Log.d("logout","displayAlreadyLoginPopup(): logout succeeded")
//                                    sessionHolder.clearActiveSession()
                                    navController.navigate(NavHostPages.matrix_login_page)
                                } catch (failure: Throwable) {
                                    Log.d("logout","displayAlreadyLoginPopup(): logout failed${failure.message.toString()}")

                                }                            }
                        }) {
                            Text("sign out")
                        }
                    }

                }

            }


        }

    }
}