package com.lihan.guessthenumbergame.repositories

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lihan.guessthenumbergame.R

class FireBaseRepository(
    val context : Context
) {
    var firebase : FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getGameRoomsRef() : DatabaseReference{
        return firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH))
    }
    fun getGameRoomsChildRef(roomFullId : String) : DatabaseReference{
        return firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(roomFullId)
    }
    fun getGameRoomsStatusChildRef(roomFullId : String) : DatabaseReference{
        return firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(roomFullId)

    }
}