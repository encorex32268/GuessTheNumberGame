package com.lihan.guessthenumbergame.repositories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.bindingViewInit
import com.lihan.guessthenumbergame.databinding.CreateroomAlertviewBinding
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.databinding.FragmentHomeBinding
import com.lihan.guessthenumbergame.other.ChoiceNumberAlertListener
import com.lihan.guessthenumbergame.other.CreateRoomAlertListener

class AlertRoomFactory(
    val context: Context
) {
    private lateinit var createRoomAlertViewBinding: CreateroomAlertviewBinding

    fun getCreateRoomAlertView(
        binding: FragmentHomeBinding,
        createRoomAlertListener: CreateRoomAlertListener
    ): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.ALERT_CRAETEROOM))
            .setView(initCreateRoomAlertViewBinding(binding))
            .setNegativeButton(context.getString(R.string.ALERT_CANCEL)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.ALERT_OK)) { _, _ ->
                var numberString = ""
                createRoomAlertViewBinding.apply {
                    val numberTextViews = arrayListOf(
                        alertNumber1TextView,
                        alertNumber2TextView,
                        alertNumber3TextView,
                        alertNumber4TextView
                    )
                    numberTextViews.forEach {
                        numberString += it.text.toString()
                    }
                }
                createRoomAlertListener.send(numberString)
            }

    }


    fun getChoiceNumberAlertView(
        binding: FragmentGameBinding,
        choiceNumberAlertListener: ChoiceNumberAlertListener
    ): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.ALERT_CRAETEROOM))
            .setView(initChoiceNumberAlertViewBinding(binding))
            .setNegativeButton(context.getString(R.string.ALERT_CANCEL)) { _, _ -> }
            .setPositiveButton(context.getString(R.string.ALERT_OK)) { _, _ ->
                var numberString = ""
                createRoomAlertViewBinding.apply {
                    val numberTextViews = arrayListOf(
                        alertNumber1TextView,
                        alertNumber2TextView,
                        alertNumber3TextView,
                        alertNumber4TextView
                    )
                    numberTextViews.forEach {
                        numberString += it.text.toString()
                    }
                }
                choiceNumberAlertListener.okClick(numberString)
            }

    }


    private fun initCreateRoomAlertViewBinding(binding: FragmentHomeBinding): View {
        val view =
            LayoutInflater.from(context).inflate(R.layout.createroom_alertview, binding.root, false)
        createRoomAlertViewBinding = CreateroomAlertviewBinding.bind(view)
        createRoomAlertViewBinding.apply {
            bindingViewInit()
        }
        return createRoomAlertViewBinding.root
    }


    private fun initChoiceNumberAlertViewBinding(binding : FragmentGameBinding) : View{
        val view =
            LayoutInflater.from(context).inflate(R.layout.createroom_alertview, binding.root, false)
        createRoomAlertViewBinding = CreateroomAlertviewBinding.bind(view)
        createRoomAlertViewBinding.apply {
            bindingViewInit()
        }
        return createRoomAlertViewBinding.root
    }




}