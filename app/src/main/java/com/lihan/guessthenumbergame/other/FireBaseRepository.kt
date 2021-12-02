package com.lihan.guessthenumbergame.other

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lihan.guessthenumbergame.R

class FireBaseRepository(
    val context : Context
) {


    fun getGameRoomsRef() : DatabaseReference{
        return FirebaseDatabase.getInstance().getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH))
    }

    fun getGameRoomsStatusRef() : DatabaseReference{
        return FirebaseDatabase.getInstance().getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH))

    }
    fun getGameRoomsChildRef(roomFullId : String) : DatabaseReference{
        return FirebaseDatabase.getInstance().getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(roomFullId)
    }
    fun getGameRoomsStatusChildRef(roomFullId : String) : DatabaseReference{
        return FirebaseDatabase.getInstance().getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(roomFullId)

    }

}