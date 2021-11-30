package com.lihan.guessthenumbergame.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom
import timber.log.Timber
import java.util.*

class HomeRepository(
    val context : Context
) {

    val gameRooms = MutableLiveData<MutableList<GameRoom>?>()

    init {
        getGameRooms()
    }
    private fun getGameRooms(){
        val firebase  = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference("GameRooms")
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

    }



}