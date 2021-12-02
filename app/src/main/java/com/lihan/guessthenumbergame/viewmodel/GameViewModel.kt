package com.lihan.guessthenumbergame.viewmodel

import androidx.lifecycle.ViewModel
import com.lihan.guessthenumbergame.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    val gameRepository: GameRepository
) : ViewModel(){

    fun getResults() = gameRepository.getResults()
    fun getRoomStatus(roomFullid: String) = gameRepository.getRoomStatus(roomFullid)
}