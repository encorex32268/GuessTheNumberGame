package com.lihan.guessthenumbergame

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.lihan.guessthenumbergame.databinding.ChoicenumberViewBinding
import com.lihan.guessthenumbergame.databinding.CreateroomAlertviewBinding



//ChoiceViewBinding
fun ChoicenumberViewBinding.bindingViewInit() {
    val numbersTextViewID = arrayListOf(
        number1TextView, number2TextView, number3TextView, number4TextView)
    numbersTextViewID.forEach { textView ->
        textView.setOnClickListener {
            textView.text = ""
        }
    }
    val choiceNumbersTextViewID = arrayListOf(
        choiceNum1TextView,choiceNum2TextView,choiceNum3TextView,choiceNum4TextView,choiceNum5TextView,
        choiceNum6TextView, choiceNum7TextView,choiceNum8TextView,choiceNum9TextView,choiceNum10TextView
    )
    choiceNumbersTextViewID.forEach { textView ->
        textView.setOnClickListener {
            var isSet = false
            numbersTextViewID.forEach {
                if (!isSet && it.text.isEmpty()){
                    isSet = !isSet
                    it.text = textView.text.toString()
                }
            }
        }
    }
}

fun ChoicenumberViewBinding.getNumbersTextViewID() = arrayListOf(
    number1TextView, number2TextView, number3TextView, number4TextView)

//CreateroomAlertviewBinding

fun CreateroomAlertviewBinding.bindingViewInit() {
    val numberTextViews = arrayListOf(
        alertNumber1TextView,
        alertNumber2TextView,
        alertNumber3TextView,
        alertNumber4TextView
    )
    numberTextViews.forEach { textView ->
        textView.setOnClickListener {
            textView.text = ""
        }
    }
    val choiceNumbersTextViewID = arrayListOf(
        alertchoiceNum1TextView,
        alertchoiceNum2TextView,
        alertchoiceNum3TextView,
        alertchoiceNum4TextView,
        alertchoiceNum5TextView,
        alertchoiceNum6TextView,
        alertchoiceNum7TextView,
        alertchoiceNum8TextView,
        alertchoiceNum9TextView,
        alertchoiceNum10TextView
    )
    choiceNumbersTextViewID.forEach { textView ->
        textView.setOnClickListener {
            var isSet = false
            numberTextViews.forEach {
                if (!isSet && it.text.isEmpty()) {
                    isSet = !isSet
                    it.text = textView.text.toString()
                }
            }
        }
    }
}
