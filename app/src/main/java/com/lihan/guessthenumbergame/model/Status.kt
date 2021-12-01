package com.lihan.guessthenumbergame.model

sealed class Status{
    object RoomCreated : Status()
    object StartGame : Status()
    object CreatorTurn : Status()
    object JoinerTurn : Status()
    object CreatorWin :Status()
    object JoinerWin : Status()
    object EndGame : Status()
}
