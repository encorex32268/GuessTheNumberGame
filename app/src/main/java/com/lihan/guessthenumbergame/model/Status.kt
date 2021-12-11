package com.lihan.guessthenumbergame.model

sealed class Status(var name : String){
    object RoomCreated : Status("Wait For Joiner")
    object StartGame : Status("StartGame")
    object CreatorEndGame :Status("CreatorEndGame")
    object JoinerEndGame: Status("JoinerEndGame")
    object EndGame : Status("EndGame")
    object CreatorExitGame : Status("CreatorExitGame")
    object JoinerExitGame : Status("JoinerExitGame")

}
