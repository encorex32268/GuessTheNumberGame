package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.ViewVisibilityListener
import com.lihan.guessthenumbergame.databinding.FragmentHomeBinding
import com.lihan.guessthenumbergame.log
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.CreateRoomAlertListener
import com.lihan.guessthenumbergame.other.Resources
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.repositories.InputNumberCheckerUtils
import com.lihan.guessthenumbergame.other.RoomClickListener
import com.lihan.guessthenumbergame.repositories.AlertRoomFactory
import com.lihan.guessthenumbergame.ui.HomeAdapter
import com.lihan.guessthenumbergame.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject



@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), RoomClickListener{
    private val TAG = HomeFragment::class.java.simpleName
    private lateinit var binding : FragmentHomeBinding
    private lateinit var homeAdapter : HomeAdapter
    private val viewModel : HomeViewModel by viewModels()
    @Inject
    lateinit var fireBaseRepository: FireBaseRepository
    lateinit var alertRoomFactory : AlertRoomFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertRoomFactory = AlertRoomFactory(requireContext())
        setUpGameRoomRecyclerView()
        setUpViewModel()

    }



    private fun setUpGameRoomRecyclerView() {
        binding.apply {
            homeGameRoomRecyclerView.apply {
                homeAdapter = HomeAdapter(mutableListOf()).also {
                    it.roomClickListener = this@HomeFragment
                }
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = homeAdapter
            }
            homeCreateGameRoomBtn.setOnClickListener {
                alertRoomFactory.getCreateRoomAlertView(binding,object : CreateRoomAlertListener{
                    override fun send(numberString: String) {
                        if (!InputNumberCheckerUtils.isCurrentNumber(numberString)) return
                        viewModel.createGameRoom(creatorIntoTheRoom(numberString))
                    }
                }).show()
            }
        }
    }

    private fun setUpViewModel() {
        lifecycleScope.launch {
            viewModel.gameRoomsUIStatus.collect {
                when (it) {
                        is HomeViewModel.HomeUIStatus.Loading -> {
                            binding.homeProgressBar.isVisible = true
                        }
                        is HomeViewModel.HomeUIStatus.Success -> {
                            binding.homeProgressBar.isVisible = false
                            homeAdapter.apply {
                                gameRooms = it.data
                                notifyDataSetChanged()
                            }
                        }
                        is HomeViewModel.HomeUIStatus.Error -> {
                            binding.homeProgressBar.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.createRoomsUIStatus.collect{
                    when(it){
                        is HomeViewModel.HomeUIStatus.Loading->{
                            binding.homeProgressBar.isVisible = true
                        }
                        is HomeViewModel.HomeUIStatus.Success->{
                            binding.homeProgressBar.isVisible = false
                            val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(it.data[0])
                            findNavController().navigate(action)
                        }
                        is HomeViewModel.HomeUIStatus.Error->{
                            binding.homeProgressBar.isVisible = false
                            Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.joinerIntoTheRoom.collect {
                    when (it) {
                        is HomeViewModel.HomeUIStatus.Loading -> {
                            binding.homeProgressBar.isVisible = true
                        }
                        is HomeViewModel.HomeUIStatus.Success -> {
                            binding.homeProgressBar.isVisible = false
                            val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(it.data[0])
                            findNavController().navigate(action)
                        }
                        is HomeViewModel.HomeUIStatus.Error -> {
                            binding.homeProgressBar.isVisible = false
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }

        }


    private fun creatorIntoTheRoom(creatorNumberString: String): GameRoom {
        val myRef = fireBaseRepository.getGameRoomsRef().push()
        val timeString = Date().time.toString()
        val id = timeString.substring(timeString.length - 4).toInt()
        val roomFullid = myRef.key!!
        return GameRoom(
            roomFullid, id, "Creator", "", 5000, creatorNumberString, ""
        )

    }

    override fun roomClick(gameRoom: GameRoom) {
        alertRoomFactory.getCreateRoomAlertView(binding,object : CreateRoomAlertListener{
            override fun send(numberString: String) {
                    if (!InputNumberCheckerUtils.isCurrentNumber(numberString)) return
                    gameRoom.apply {
                        joinerAnswer = numberString
                        joiner = "Joiner"
                    }
                    viewModel.joinerIntoTheRoom(gameRoom)
            }
        }).setTitle(getString(R.string.ALERT_JOINROOM))
        .show()

    }




}