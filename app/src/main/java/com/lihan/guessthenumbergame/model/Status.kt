package com.lihan.guessthenumbergame.model

sealed class Status(var name : String){
    object RoomCreated : Status("RoomCreated")
    object StartGame : Status("StartGame")
    object CreatorTurn : Status("CreatorTurn")
    object JoinerTurn : Status("JoinerTurn")
    object CreatorWin :Status("CreatorWin")
    object JoinerWin : Status("JoinerWin")
    object EndGame : Status("EndGame")
}
