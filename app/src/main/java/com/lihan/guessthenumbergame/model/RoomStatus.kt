package com.lihan.guessthenumbergame.model

data class RoomStatus(
    val roomFullID : String,
    val status : String,
    val joinersGuess : Int ,
    val creatorGuess : Int ,
){
    constructor():this("","",0,0)
}
