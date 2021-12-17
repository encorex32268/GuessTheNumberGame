package com.lihan.guessthenumbergame.status

sealed class GameRemoveUIStatus{
    object Success : GameRemoveUIStatus()
    data class Error(val message : String ) : GameRemoveUIStatus()
    object Loading : GameRemoveUIStatus()
    object Empty : GameRemoveUIStatus()
}