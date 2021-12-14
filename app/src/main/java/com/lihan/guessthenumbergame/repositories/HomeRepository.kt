package com.lihan.guessthenumbergame.repositories

import android.content.Context
import android.util.Log.d
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom
import timber.log.Timber
import timber.log.Timber.Forest.d
import java.util.*
import kotlin.collections.ArrayList

class HomeRepository(
    val context : Context
) {

    private val gameRooms = MutableLiveData<ArrayList<GameRoom>>()
    init {
        getGameRooms()
    }
    fun getGameRooms() : MutableLiveData<ArrayList<GameRoom>>{
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
        return gameRooms

    }



}