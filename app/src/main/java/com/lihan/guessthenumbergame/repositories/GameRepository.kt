package com.lihan.guessthenumbergame.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus

class GameRepository(
    val context : Context
) {

    private val mGameRoom = MutableLiveData<GameRoom>()
    private val mRoomStatus = MutableLiveData<RoomStatus>()

    fun getGameRoom(roomFullId : String): MutableLiveData<GameRoom> {
        val firebase  = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference("GameRooms").child(roomFullId)
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameRoom  = snapshot.getValue(GameRoom::class.java)
                gameRoom?.let {
                    mGameRoom.postValue(it)
                }
            }
            override fun onCancelled(error: DatabaseError) {} })

        return mGameRoom
    }


    fun getRoomStatus(roomFullId : String) : MutableLiveData<RoomStatus>{
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference("GameRoomStatus").child(roomFullId)
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomStatus = snapshot.getValue(RoomStatus::class.java)
                roomStatus?.let {
                    mRoomStatus.postValue(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {} })

        return mRoomStatus
    }

}