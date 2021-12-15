package com.lihan.guessthenumbergame.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.Resources

class GameRepository(
    val context : Context
) {

    private val mRoomStatus = MutableLiveData<RoomStatus>()
    private val mGameRoom = MutableLiveData<GameRoom>()

    private val removeGameRoomResult = MutableLiveData<Resources>()
    private val removeJoinerResult = MutableLiveData<Resources>()

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

    fun getRemoveGameRoomAndStatus() = removeGameRoomResult

    fun removeJoinerInGameRoom(gameRoom: GameRoom){
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(gameRoom.roomFullId).setValue(
            gameRoom.apply {
                joiner = ""
                joinerAnswer =""
            }
        ).addOnCompleteListener {
            if(taskHandler(it) == Resources.Success){
                removeGameRoomResult.postValue(Resources.Success)
            }

        }
    }

    fun setRoomStatus(mRoomStatus: RoomStatus) {
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(mRoomStatus.roomFullID).setValue(mRoomStatus)
    }

    fun setGameRoom(gameRoom: GameRoom) {
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(gameRoom.roomFullId).setValue(gameRoom)
    }

    fun removeGameRoomAndStatus(roomFullID: String) {
        val firebase = FirebaseDatabase.getInstance()
        firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(roomFullID).removeValue().addOnCompleteListener {
            if (taskHandler(it) == Resources.Success){
                firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(roomFullID).removeValue().addOnCompleteListener{ gameRooms ->
                    removeGameRoomResult.postValue(taskHandler(it))
                    mRoomStatus.postValue(RoomStatus())
                    mGameRoom.postValue(GameRoom())
                }
            }
        }
    }


    private fun taskHandler(task : Task<Void>) : Resources {
        var result  : Resources = Resources.Loading
        when {
            task.isSuccessful -> {
                result = Resources.Success
            }
            task.isCanceled -> {
                result = Resources.Fail
            }
            task.isComplete ->{
                result = Resources.Success
            }
        }
        return result
    }

    fun getRemoveJoinerInGameRoom() = removeJoinerResult


}