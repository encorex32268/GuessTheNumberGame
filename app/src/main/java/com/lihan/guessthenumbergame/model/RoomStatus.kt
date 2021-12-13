package com.lihan.guessthenumbergame.model

data class RoomStatus(
    val roomFullID : String,
    var status : String,
    var joinersGuess : Int ,
    var creatorGuess : Int ,
){
    constructor():this("","",0,0)

    override fun toString(): String {
        return "RoomStatus(roomFullID='$roomFullID', status='$status', joinersGuess=$joinersGuess, creatorGuess=$creatorGuess)"
    }


}
