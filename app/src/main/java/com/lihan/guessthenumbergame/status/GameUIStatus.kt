package com.lihan.guessthenumbergame.status

sealed class GameUIStatus{
    data class Success<T>(val data : T) : GameUIStatus()
    data class Error(val message : String ) : GameUIStatus()
    object Loading : GameUIStatus()
    object Empty : GameUIStatus()
}
