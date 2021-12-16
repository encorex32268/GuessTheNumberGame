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
import com.lihan.guessthenumbergame.log
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    @Inject
    lateinit var firebaseRepository: FireBaseRepository


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
                    log("MainActivity displayName ${destination.displayName}")
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
