package com.lihan.guessthenumbergame

import java.lang.NumberFormatException
import android.os.CountDownTimer




fun main() {




}


private fun checkInputNumber(number : Int) : Boolean{
    if (number.toString().length<4) return false
    val hashSet = HashSet<String>()
    number.toString().toCharArray().forEach {
        hashSet.add(it.toString())
    }
    if (hashSet.size<4)return false
    return true
}