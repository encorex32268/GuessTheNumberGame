package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.repositories.InputNumberCheckerUtils
import com.lihan.guessthenumbergame.other.RoomClickListener
import com.lihan.guessthenumbergame.repositories.AlertRoomFactory
import com.lihan.guessthenumbergame.ui.HomeAdapter
import com.lihan.guessthenumbergame.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject



@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), RoomClickListener{

    private lateinit var binding : FragmentHomeBinding
    private lateinit var homeAdapter : HomeAdapter
    private val viewModel : HomeViewModel by viewModels()
    @Inject
    lateinit var fireBaseRepository: FireBaseRepository
    lateinit var alertRoomFactory : AlertRoomFactory

    private lateinit var mGameRoom: GameRoom

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertRoomFactory = AlertRoomFactory(requireContext())
        setUpGameRoomRecyclerView()

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
                        binding.homeProgressBar.setVisibility(View.VISIBLE,true)
                        mGameRoom = creatorIntoTheRoom(numberString)
                        fireBaseRepository.getGameRoomsChildRef(mGameRoom.roomFullId).setValue(mGameRoom).addOnCompleteListener {
                            val roomStatus = RoomStatus(mGameRoom.roomFullId, Status.RoomCreated.name,0,0)
                            fireBaseRepository.getGameRoomsStatusChildRef(mGameRoom.roomFullId).setValue(roomStatus).addOnCompleteListener {
                                binding.homeProgressBar.setVisibility(View.INVISIBLE,false)
                            }
                        }
                    }
                }).show()
            }
            viewModel.getGameRooms().observe(viewLifecycleOwner,{
                        homeGameRoomRecyclerView.apply {
                            homeAdapter = HomeAdapter(it).also {
                                it.roomClickListener = this@HomeFragment
                            }
                            adapter = homeAdapter
                        }


            })
            homeProgressBar.visibilityListener = object : ViewVisibilityListener{
                override fun doSomeTing() {
                    log("doSomeThing $mGameRoom")
                    binding.root.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToGameFragment(mGameRoom))

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
                    mGameRoom = gameRoom
                    joinerIntoTheRoom(mGameRoom)

            }

        }).setTitle(getString(R.string.ALERT_JOINROOM))
        .show()

    }

    private fun joinerIntoTheRoom(gameRoom: GameRoom) {
        binding.homeProgressBar.setVisibility(View.VISIBLE,true)
        fireBaseRepository.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom).addOnCompleteListener {
            val roomStatus = RoomStatus(gameRoom.roomFullId, Status.StartGame.name,0,0)
            fireBaseRepository.getGameRoomsStatusChildRef(gameRoom.roomFullId).setValue(roomStatus).addOnCompleteListener {
                binding.homeProgressBar.setVisibility(View.INVISIBLE,false)
            }
        }

    }



}