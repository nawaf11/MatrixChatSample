package com.example.matrixchat.ui.chat_app

import android.app.Application
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.events.model.isTextMessage
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.util.toMatrixItem


class MatrixRoomDetailsViewModel constructor(context : Application) : AndroidViewModel(context) {

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

                        snapshot.forEachIndexed { i, item ->
                            val event = currentSession.eventService().getEvent(room.roomId, snapshot[i].eventId)
                            Log.d("TimeLineListener", "onTimelineUpdated  type of $i ==> ${event.type}, isTextMessage: ${event.isTextMessage()} , content.size ${event.content?.size}")
                            if(event.isTextMessage()) {
                                Log.d("TimeLineListener", "TextMessage Item, $i")
//                                textEventList.add("TextMessage Item, $i,  Sender ==> ${event.sendState.name}")
                                val c=event.senderId?.let {
                                    currentSession.roomService().getRoomMember(it, item.roomId) }?.toMatrixItem()
                                textEventList.add(" ${c?.displayName + ":"}${item.getLastMessageContent()?.body}")

//                                event.content?.values?.forEachIndexed { i,  mValue ->
//                                    Log.d("TimeLineListener", "onTimelineUpdated  $i ==> value = ${mValue.toString()} ")
//                                }
                            }
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
fun MatrixRoomDetails(navController: NavController, currentSession : Session, roomId : String, viewModel : MatrixRoomDetailsViewModel = viewModel()) {

    val roomDetails = currentSession.roomService().getRoom(roomId)
    val messageListData by viewModel.messagesList

    roomDetails?.let {
        viewModel.setupRoomListener(currentSession, roomDetails) // Timeline Listner

        val numberOfJoinedMembers = roomDetails.membershipService()?.getNumberOfJoinedMembers()

        Surface(modifier = Modifier.fillMaxWidth()) {

            Column {
                Text(text = "Room-Details:\n\nnumberOfJoinedMembers == > $numberOfJoinedMembers",
                    Modifier.padding(6.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start)


                LazyColumn() {

                    items(count = messageListData.size) { index ->
                        Box(modifier = Modifier
                            .padding(10.dp)
                            .background(Color.Gray)) {

                            Box(modifier = Modifier.padding(10.dp)) {
                                Text(text = messageListData[index], color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(0.dp))
                            }
                        }

                    }

                }

            }


        }

    }
}