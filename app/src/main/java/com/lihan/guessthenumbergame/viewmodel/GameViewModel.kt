package com.lihan.guessthenumbergame.viewmodel

import androidx.lifecycle.ViewModel
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.other.Resources
import com.lihan.guessthenumbergame.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    val gameRepository: GameRepository
) : ViewModel(){

    fun getRoomStatus(roomFullID: String) = gameRepository.getRoomStatus(roomFullID)
    fun getGameRoom(roomFullID: String) = gameRepository.getGameRoom(roomFullID)
    fun getRemoveGameRoomAndStatus() = gameRepository.getRemoveGameRoomAndStatus()



    fun removeJoinerInGameRoom(gameRoom : GameRoom) = gameRepository.removeJoinerInGameRoom(gameRoom)

    //functions
    fun setRoomStatus(mRoomStatus: RoomStatus)  = gameRepository.setRoomStatus(mRoomStatus)
    fun setGameRoom(gameRoom : GameRoom)  = gameRepository.setGameRoom(gameRoom)
    fun removeGameRoomAndStatus(roomFullID: String) = gameRepository.removeGameRoomAndStatus(roomFullID)


}