package com.lihan.guessthenumbergame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.lihan.guessthenumbergame.databinding.ActivityMain2Binding
import com.lihan.guessthenumbergame.databinding.ChoicenumberViewBinding
import timber.log.Timber
import kotlin.math.log

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding:ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding.apply {

            button.setOnClickListener {
//                textView.append("Text \n")
//                scrollView2.post {
//                    scrollView2.fullScroll(ScrollView.FOCUS_DOWN)
//                }

                val view = LayoutInflater.from(this@MainActivity2).inflate(R.layout.choicenumber_view,binding.root,false)
                val choicenumberViewBinding = ChoicenumberViewBinding.bind(view)

                AlertDialog.Builder(this@MainActivity2)
                    .setTitle("Test")
                    .setView(choicenumberViewBinding.root)
                    .setPositiveButton("ok") { _, _ ->
                            log("OK")
                    }.setNegativeButton("Cancel"){ _, _ ->
                            log("Cancel")
                    }
                    .show()
            }



        }

    }
}