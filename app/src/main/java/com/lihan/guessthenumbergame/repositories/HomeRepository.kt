package com.lihan.guessthenumbergame.repositories

import android.content.Context
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.Resources
import timber.log.Timber
import timber.log.Timber.Forest.d
import java.util.*
import kotlin.collections.ArrayList

class HomeRepository(
    val context : Context
) {

    private val gameRooms = MutableLiveData<ArrayList<GameRoom>>()

    var result = MutableLiveData<Resources>()

    init {
        getGameRooms()
    }
    fun getGameRooms() : MutableLiveData<ArrayList<GameRoom>>{
        val firebase  = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH))
        myRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val nowData = arrayListOf<GameRoom>()
                snapshot.children.forEach {
                    val gameRoom = it.getValue(GameRoom::class.java)
                    gameRoom?.let {
                        nowData.add(it)
                    }
                }
                gameRooms.postValue(nowData)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        return gameRooms

    }

    fun getFirebaseResult() : MutableLiveData<Resources> = result


    fun createGameRoom(mGameRoom : GameRoom){
        result.postValue(Resources.Loading)
        val firebase = FirebaseDatabase.getInstance()
        val myRefGameRoom = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(mGameRoom.roomFullId)
        val myRefGameRoomStatus = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(mGameRoom.roomFullId)
        myRefGameRoom.setValue(mGameRoom).addOnCompleteListener {
            if (it.isSuccessful){
                myRefGameRoomStatus.setValue(RoomStatus(
                    mGameRoom.roomFullId,
                    Status.RoomCreated.name,
                    0,0
                )).addOnCompleteListener {
                    if (it.isSuccessful){
                        result.postValue(Resources.Success)
                    }else{
                        result.postValue(Resources.Fail)
                    }
                }
            }else{
                result.postValue(Resources.Fail)
            }
        }






    }




}