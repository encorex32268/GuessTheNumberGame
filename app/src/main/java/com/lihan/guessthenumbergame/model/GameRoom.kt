package com.lihan.guessthenumbergame.model

data class GameRoom(
    var id  : Int ,
    var creator : String ,
    var joiner: String ,
    var waitTime : Int ,
    var creatorAnswer : String,
    var joinerAnswer : String
){
    constructor():this(0,"","",5000,"","")

    override fun toString(): String {
        return "GameRoom(id=$id, creator='$creator', joiner='$joiner', waitTime=$waitTime, creatorAnswer='$creatorAnswer', joinerAnswer='$joinerAnswer')"
    }

}
