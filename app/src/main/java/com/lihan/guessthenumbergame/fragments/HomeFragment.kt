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
import com.google.firebase.database.FirebaseDatabase
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.CreateroomAlertviewBinding
import com.lihan.guessthenumbergame.databinding.FragmentHomeBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.RoomClickListener
import com.lihan.guessthenumbergame.ui.HomeAdapter
import com.lihan.guessthenumbergame.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException
import java.util.*
import kotlin.collections.HashSet

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), RoomClickListener {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var alertCustomBinding : CreateroomAlertviewBinding

    private lateinit var homeAdapter : HomeAdapter
    private val viewModel : HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        alertCustomBinding = CreateroomAlertviewBinding.inflate(layoutInflater)
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        val numberString  = alertCustomBinding.creatorAnswerEditTextView.editableText.toString()
                        if (checkInputNumber(numberString)){
                            val creatorNumber  = numberString.toInt()
                            val gameRoom = createGameRoom(creatorNumber)
                            val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                            findNavController().navigate(action)
                        }



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

    private fun createGameRoom(creatorNumber: Int): GameRoom {
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(getString(R.string.FIREBASE_GAMEROOMS_PATH)).push()
        val timeString = Date().time.toString()
        val formatedKey = timeString.substring(timeString.length - 4).toInt()
        val roomFullid = myRef.key!!
        val gameRoom = GameRoom(
            roomFullid,formatedKey, "me", "", 5000, creatorNumber, 0
        )
        myRef.setValue(gameRoom)

        val statusRef = firebase.getReference(getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(myRef.key!!)
        val roomStatus = RoomStatus(roomFullid, Status.RoomCreated.name,0,0)
        statusRef.setValue(roomStatus)

        return gameRoom

    }

    override fun roomClick(gameRoom: GameRoom) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.ALERT_JOINROOM))
            .setView(alertCustomBinding.root)
            .setPositiveButton(getString(R.string.ALERT_OK)) { _, _, ->
                val numberString  = alertCustomBinding.creatorAnswerEditTextView.editableText.toString()

                if (checkInputNumber(numberString)){
                    val creatorNumber = numberString.toInt()
                    gameRoom.apply {
                        joinerAnswer = creatorNumber
                        joiner = "Joiner"
                    }
                    updateGameRoom(gameRoom)
                    val action = HomeFragmentDirections.actionHomeFragmentToGameFragment(gameRoom)
                    findNavController().navigate(action)
                }
            }
            .setNegativeButton(getString(R.string.ALERT_CANCEL)) { _, _, -> }.show()



    }

    private fun updateGameRoom(gameRoom: GameRoom) {
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(getString(R.string.FIREBASE_GAMEROOMS_PATH)).child(gameRoom.roomFullId)
        myRef.setValue(gameRoom)
        val statusRef = firebase.getReference(getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH)).child(gameRoom.roomFullId)
        val roomStatus = RoomStatus(gameRoom.roomFullId, Status.CreatorTurn.name,0,0)
        statusRef.setValue(roomStatus)
    }

    private fun checkInputNumber(numberString : String) : Boolean{
        var number: Int
        try {
            number = numberString.toInt()
        }catch (e : NumberFormatException){
            return false
        }
        if (number.toString().length<4) return false
        val hashSet = HashSet<String>()
        number.toString().toCharArray().forEach {
            hashSet.add(it.toString())
        }
        if (hashSet.size<4)return false
        return true
    }

}