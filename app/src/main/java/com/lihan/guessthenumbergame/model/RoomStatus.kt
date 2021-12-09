package com.lihan.guessthenumbergame.model

data class RoomStatus(
    val roomFullID : String,
    var status : String,
    var joinersGuess : Int ,
    var creatorGuess : Int ,
){
    constructor():this("","",0,0)
}
