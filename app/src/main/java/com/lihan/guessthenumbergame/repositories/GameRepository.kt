package com.lihan.guessthenumbergame.repositories

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status

class GameRepository(
    val context : Context
) {

    private val mRoomStatus = MutableLiveData<RoomStatus>()
    private val mGameRoom = MutableLiveData<GameRoom>()

    fun getRoomStatus(roomFullID : String) : MutableLiveData<RoomStatus>{
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(roomFullID)
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

    fun getGameRoom(roomFullID: String): MutableLiveData<GameRoom> {
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(roomFullID)
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val gameRoom  = snapshot.getValue(GameRoom::class.java)
                gameRoom?.let {
                    mGameRoom.postValue(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return mGameRoom
    }

    fun removeGameRoomAndStatus(roomFullID: String){
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(roomFullID).removeValue().addOnCompleteListener {
            firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(roomFullID).removeValue()
        }
    }

    fun removeJoinerInGameRoom(gameRoom: GameRoom){
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(gameRoom.roomFullId).setValue(
            gameRoom.apply {
                joiner = ""
                joinerAnswer =""
            }
        ).addOnCompleteListener {
            firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(gameRoom.roomFullId).child("status")
                .setValue(Status.RoomCreated.name)
        }
    }

    fun setRoomStatus(mRoomStatus: RoomStatus) {
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(mRoomStatus.roomFullID).setValue(mRoomStatus)
    }
}