package com.lihan.guessthenumbergame.model

import android.os.Parcel
import android.os.Parcelable

data class GameRoom(
    var roomFullId: String,
    var id: Int,
    var creator: String,
    var joiner: String,
    var waitTime: Int,
    var creatorAnswer: String,
    var joinerAnswer: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:""
    ) {
    }

    constructor():this("",0,"","",5000,"1234","5678")

    override fun toString(): String {
        return "GameRoom(roomFullID = ${roomFullId } id=$id, creator='$creator', joiner='$joiner', waitTime=$waitTime, creatorAnswer='$creatorAnswer', joinerAnswer='$joinerAnswer')"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomFullId)
        parcel.writeInt(id)
        parcel.writeString(creator)
        parcel.writeString(joiner)
        parcel.writeInt(waitTime)
        parcel.writeString(creatorAnswer)
        parcel.writeString(joinerAnswer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameRoom> {
        override fun createFromParcel(parcel: Parcel): GameRoom {
            return GameRoom(parcel)
        }

        override fun newArray(size: Int): Array<GameRoom?> {
            return arrayOfNulls(size)
        }
    }

}
