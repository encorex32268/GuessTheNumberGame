package com.lihan.guessthenumbergame.other

sealed class Resources{
    object Loading  : Resources()
    object Success :  Resources()
    object Fail : Resources()
}
