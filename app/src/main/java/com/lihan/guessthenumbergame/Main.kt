package com.lihan.guessthenumbergame

import java.lang.NumberFormatException

fun main() {
    println("${checkInputNumber(1234)}")
    println("${checkInputNumber(1224)}")
    println("${checkInputNumber(234)}")
    println("${checkInputNumber(0)}")


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