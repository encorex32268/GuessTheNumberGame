package com.lihan.guessthenumbergame.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.CreateroomAlertviewBinding
import com.lihan.guessthenumbergame.databinding.FragmentHomeBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.other.InputNumberCheckerUtils
import com.lihan.guessthenumbergame.other.RoomClickListener
import com.lihan.guessthenumbergame.ui.HomeAdapter
import com.lihan.guessthenumbergame.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), RoomClickListener {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var alertCustomBinding : CreateroomAlertviewBinding
    private lateinit var homeAdapter : HomeAdapter
    private val viewModel : HomeViewModel by viewModels()
    @Inject
    lateinit var fireBaseRepository: FireBaseRepository

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
        initAlertBinding()
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
                AlertDialog.Builder(requireContext())
                    .setView(alertCustomBinding.root)
                    .setTitle(getString(R.string.ALERT_CRAETEROOM))
                    .setPositiveButton(getString(R.string.ALERT_OK)) { _, _ ->
                        var numberString = ""
                        alertCustomBinding.apply {
                            val numberTextViews = arrayListOf(
                                alertNumber1TextView,alertNumber2TextView,alertNumber3TextView,alertNumber4TextView
                            )
                            numberTextViews.forEach {
                                numberString+=it.text.toString()
                            }
                        }

                            val gameRoom = createGameRoom(numberString)
                            val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                            findNavController().navigate(action)
                    }
                    .setNegativeButton(getString(R.string.ALERT_CANCEL)){ _ , _ -> }
                    .show()
            }
            viewModel.getGameRooms().observe(viewLifecycleOwner,{
                it?.let {
                    if (it.size != 0){
                        homeGameRoomRecyclerView.apply {
                            homeAdapter = HomeAdapter(it).also {
                                it.roomClickListener = this@HomeFragment
                            }
                            adapter = homeAdapter
                        }
                    }
                }

            })

        }
    }

    private fun initAlertBinding(){
        val createroomView = LayoutInflater.from(requireContext()).inflate(R.layout.createroom_alertview,binding.root,false)
        alertCustomBinding = CreateroomAlertviewBinding.bind(createroomView)
        alertCustomBinding.apply {
            val numberTextViews = arrayListOf(
                alertNumber1TextView,alertNumber2TextView,alertNumber3TextView,alertNumber4TextView
            )
            numberTextViews.forEach { textView ->
                textView.setOnClickListener {
                    textView.text = ""
                }
            }
            val choiceNumbersTextViewID = arrayListOf(
                alertchoiceNum1TextView,alertchoiceNum2TextView,alertchoiceNum3TextView,alertchoiceNum4TextView,alertchoiceNum5TextView,
                alertchoiceNum6TextView,alertchoiceNum7TextView,alertchoiceNum8TextView,alertchoiceNum9TextView,alertchoiceNum10TextView
            )
            choiceNumbersTextViewID.forEach { textView ->
                textView.setOnClickListener {
                    var isSet = false
                    numberTextViews.forEach {
                        if (!isSet && it.text.isEmpty()){
                            isSet = !isSet
                            it.text = textView.text.toString()
                        }
                    }
                }
            }
        }
    }

    private fun createGameRoom(creatorNumberString: String): GameRoom {
        val myRef = fireBaseRepository.getGameRoomsRef().push()
        val timeString = Date().time.toString()
        val formatedKey = timeString.substring(timeString.length - 4).toInt()
        val roomFullid = myRef.key!!
        val gameRoom = GameRoom(
            roomFullid,formatedKey, "me", "", 5000, creatorNumberString, ""
        )
        myRef.setValue(gameRoom)

        val roomStatus = RoomStatus(roomFullid, Status.RoomCreated.name,0,0)
        fireBaseRepository.getGameRoomsStatusChildRef(roomFullid).setValue(roomStatus)

        return gameRoom

    }

    override fun roomClick(gameRoom: GameRoom) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.ALERT_JOINROOM))
            .setView(alertCustomBinding.root)
            .setPositiveButton(getString(R.string.ALERT_OK)) { _, _ ->
                var numberString = ""
                alertCustomBinding.apply {
                    val numberTextViews = arrayListOf(
                        alertNumber1TextView,alertNumber2TextView,alertNumber3TextView,alertNumber4TextView
                    )
                    numberTextViews.forEach {
                        numberString+=it.text
                    }
                }
                if (!InputNumberCheckerUtils.isCurrentNumber(numberString)) return@setPositiveButton else{
                    gameRoom.apply {
                        joinerAnswer = numberString
                        joiner = "Joiner"
                    }
                    updateGameRoom(gameRoom)
                    val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                    findNavController().navigate(action)
                }

            }
            .setNegativeButton(getString(R.string.ALERT_CANCEL)) { _, _ -> }.show()



    }

    private fun updateGameRoom(gameRoom: GameRoom) {
        fireBaseRepository.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom)
        val roomStatus = RoomStatus(gameRoom.roomFullId, Status.StartGame.name,0,0)
        fireBaseRepository.getGameRoomsStatusChildRef(gameRoom.roomFullId).setValue(roomStatus)
    }



}