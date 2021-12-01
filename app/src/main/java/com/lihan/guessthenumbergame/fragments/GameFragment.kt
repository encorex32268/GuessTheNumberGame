package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.Constants
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemBackBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemFrontBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding : FragmentGameBinding
    private lateinit var flipViewBindingFront : NumberCardItemFrontBinding
    private lateinit var flipViewBindingBack : NumberCardItemBackBinding

    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()

    private lateinit var mGameRoom : GameRoom

    sealed class Player{
        object Creator : Player()
        object Joiner : Player()
    }
    private lateinit var player: Player
    private var otherSideAnswer = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flipViewBindingFront = NumberCardItemFrontBinding.inflate(inflater,container,false)
        flipViewBindingBack  = NumberCardItemBackBinding.inflate(inflater,container,false)
        binding = FragmentGameBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            args.gameRoom.apply {
                player = if (checkIsCreator(joiner)){
                    roomStatusTextView.text = Constants.CREATE_WAITING_FOR_JOINER
                    Player.Creator
                }else{
                    roomStatusTextView.text = Constants.JOINER_CREATOR_TURN
                    Player.Joiner
                }
                mGameRoom = this
            }

            viewModel.getRoomStatus(mGameRoom.roomFullId).observe(viewLifecycleOwner,{
                when(player){
                    is Player.Creator->{
                        when(it.status){
                            Status.RoomCreated.name ->{}
                            Status.CreatorTurn.name ->{
                                getGameRoomFromFireBase()
                                roomStatusTextView.text = "Your Turn"
                            }
                            Status.JoinerTurn.name ->{

                            }
                            Status.CreatorWin.name ->{

                            }
                            Status.JoinerWin.name ->{

                            }
                            Status.EndGame.name ->{

                            }

                        }
                    }
                    is Player.Joiner->{
                        when(it.status){
                            Status.RoomCreated.name -> { }
                            Status.CreatorTurn.name ->{ getGameRoomFromFireBase() }
                            Status.JoinerTurn.name ->{

                            }
                            Status.CreatorWin.name ->{

                            }
                            Status.JoinerWin.name ->{

                            }
                            Status.EndGame.name ->{

                            }
                        }
                    }
                }
            })

        }
    }

    private fun checkIsCreator(joiner: String): Boolean {
        return joiner.isEmpty()||joiner.isBlank()
    }


    private fun getGameRoomFromFireBase(){
        val firebase  = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference("GameRooms").child(mGameRoom.roomFullId)
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                val gameRoom  = snapshot.getValue(GameRoom::class.java)
                gameRoom?.let {
                    mGameRoom = it
                    otherSideAnswer = when(player){
                        is Player.Creator->{
                            it.joinerAnswer
                        }
                        is Player.Joiner->{
                            it.creatorAnswer
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {} }
        )
    }

}