package com.lihan.guessthenumbergame.other

import java.lang.NumberFormatException

class InputNumberCheckerUtils {

    companion object{
        fun isCurrentNumber(numberString : String) : Boolean{
            val number: Int
            try {
                number = numberString.toInt()
            }catch (e : NumberFormatException){
                return false
            }
            if (number.toString().length<4) return false
            val hashSet = HashSet<String>()
            number.toString().toCharArray().forEach {
                hashSet.add(it.toString())
            }
            if (hashSet.size<4)return false
            return true
        }
    }
}