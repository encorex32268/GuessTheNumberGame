package com.lihan.guessthenumbergame

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar

class CustomProgressBar @JvmOverloads constructor (
    context: Context?,
    attrs: AttributeSet?,
) : ProgressBar(context, attrs) {

    lateinit var visibilityListener: ViewVisibilityListener

    fun setVisibility(visibility: Int,loading : Boolean) {
        if (visibility == View.INVISIBLE && !loading){
            visibilityListener.doSomeTing()
        }
        super.setVisibility(visibility)
    }


}