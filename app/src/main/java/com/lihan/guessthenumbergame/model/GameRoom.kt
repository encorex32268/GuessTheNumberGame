package com.lihan.guessthenumbergame.model

import java.util.*

data class GameRoom(
    var id  : Int ,
    var creator : String ,
    var joiner: String ,
    var waitTime : Int ,
    var creatorAnswer : String,
    var joinerAnswer : String
){
    constructor():this(0,"","",5000,"","")
}
