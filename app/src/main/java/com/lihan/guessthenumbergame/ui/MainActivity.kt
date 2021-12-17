package com.lihan.guessthenumbergame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        binding.apply {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
            bottomNav.apply {
                setupWithNavController(navHostFragment!!.findNavController())
                navHostFragment.findNavController().addOnDestinationChangedListener { controller, destination, arguments ->
                    when(destination.id){
                        R.id.homeFragment,R.id.memberFragment->{
                            bottomNav.visibility = View.VISIBLE
                        }
                        else ->{
                            bottomNav.visibility = View.INVISIBLE
                        }
                    }

                }

            }
        }
    }


}
