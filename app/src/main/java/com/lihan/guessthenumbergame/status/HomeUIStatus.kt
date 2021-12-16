package com.lihan.guessthenumbergame.status

import com.lihan.guessthenumbergame.model.GameRoom

sealed class HomeUIStatus {
    object Loading : HomeUIStatus()
    object Empty : HomeUIStatus()
    data class Success(val data: ArrayList<GameRoom>) : HomeUIStatus()
    data class Error(val message: String) : HomeUIStatus()
}