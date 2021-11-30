package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.FragmentHomeBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.ui.HomeAdapter
import com.lihan.guessthenumbergame.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var homeAdapter : HomeAdapter
    private val viewModel : HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpGameRoomRecyclerView()
    }

    private fun setUpGameRoomRecyclerView() {
        binding.apply {
            homeGameRoomRecyclerView.apply {
                homeAdapter = HomeAdapter(mutableListOf())
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeAdapter
            }
            homeCreateGameRoomBtn.setOnClickListener {
                //Firebase insert
//                dumpData()




            }
            viewModel.getGameRooms().observe(viewLifecycleOwner,{
                it?.let {
                    if (it.size != 0){
                        homeGameRoomRecyclerView.apply {
                            homeAdapter = HomeAdapter(it)
                            adapter = homeAdapter
                        }
                    }
                }

            })

        }
    }

    private fun dumpData() {
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference("GameRooms").push()
        val timeString = Date().time.toString()
        val formatedKey = timeString.substring(timeString.length - 4).toInt()
        val gameRoom = GameRoom(
            formatedKey, "me", "you", 5000, "1234", "5678"
        )
        myRef.setValue(gameRoom)
    }

}