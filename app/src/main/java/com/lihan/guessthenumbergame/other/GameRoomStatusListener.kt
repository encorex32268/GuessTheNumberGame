package com.lihan.guessthenumbergame.other

interface GameRoomStatusListener {

    suspend fun wCreateRoom()
    suspend fun wGameRoom()
    suspend fun wRoomStatus()
    suspend fun wRemoveGameRoom()
    suspend fun wRemoveJoiner()
    suspend fun wJoinerIntoRoom()

}