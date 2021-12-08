package com.lihan.guessthenumbergame

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import timber.log.Timber

object Constants {

    const val CREATE_WAITING_FOR_JOINER = "Waiting for joiner"
    const val CREATE_JOINER_TURN = "Joiner Turn"
    const val JOINER_CREATOR_TURN = "Creator Turn"
}


fun Fragment.log(message : String){
    Timber.d("$message")
}
fun AppCompatActivity.log(message : String){
    Timber.d("$message")
}