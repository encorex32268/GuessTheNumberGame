package com.lihan.guessthenumbergame.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom

class GameRepository(
    val context : Context
) {

    private val mGameRoom = MutableLiveData<GameRoom>()

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



}