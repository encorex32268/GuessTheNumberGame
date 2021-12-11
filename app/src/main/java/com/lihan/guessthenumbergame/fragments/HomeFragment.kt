package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lihan.guessthenumbergame.R
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
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
                        val gameRoom = creatorIntoTheRoom(numberString)
                        val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                        findNavController().navigate(action)
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
        }
    }

    private fun creatorIntoTheRoom(creatorNumberString: String): GameRoom {
        val myRef = fireBaseRepository.getGameRoomsRef().push()
        val timeString = Date().time.toString()
        val id = timeString.substring(timeString.length - 4).toInt()
        val roomFullid = myRef.key!!
        val gameRoom = GameRoom(
            roomFullid,id, "Creator", "", 5000, creatorNumberString, ""
        )
        myRef.setValue(gameRoom)

        val roomStatus = RoomStatus(roomFullid, Status.RoomCreated.name,0,0)
        fireBaseRepository.getGameRoomsStatusChildRef(roomFullid).setValue(roomStatus)

        return gameRoom

    }

    override fun roomClick(gameRoom: GameRoom) {
        alertRoomFactory.getCreateRoomAlertView(binding,object : CreateRoomAlertListener{
            override fun send(numberString: String) {
                    if (!InputNumberCheckerUtils.isCurrentNumber(numberString)) return
                    gameRoom.apply {
                        joinerAnswer = numberString
                        joiner = "Joiner"
                    }
                    joinerIntoTheRoom(gameRoom)
                    val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                    findNavController().navigate(action)
            }

        }).setTitle(getString(R.string.ALERT_JOINROOM))
        .show()

    }

    private fun joinerIntoTheRoom(gameRoom: GameRoom) {
        fireBaseRepository.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom)
        val roomStatus = RoomStatus(gameRoom.roomFullId, Status.StartGame.name,0,0)
        fireBaseRepository.getGameRoomsStatusChildRef(gameRoom.roomFullId).setValue(roomStatus)
    }



}