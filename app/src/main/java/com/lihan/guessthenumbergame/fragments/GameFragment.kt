package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.lihan.guessthenumbergame.Constants
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemBackBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemFrontBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding : FragmentGameBinding
    private lateinit var flipViewBindingFront : NumberCardItemFrontBinding
    private lateinit var flipViewBindingBack : NumberCardItemBackBinding

    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()

    private lateinit var gameRoom : GameRoom
    private var isCreator = false

    sealed class Player{
        object Creator : Player()
        object Joiner : Player()
    }
    private lateinit var player: Player


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
            gameRoom = args.gameRoom.apply {
                player = if (checkIsCreator(joiner)){
                    roomStatusTextView.text = Constants.CREATE_WAITING_FOR_JOINER
                    Player.Creator
                }else{
                    roomStatusTextView.text = Constants.JOINER_CREATOR_TURN
                    Player.Joiner
                }
            }

            viewModel.getGameRoom(gameRoom.roomFullId).observe(viewLifecycleOwner,{
                it?.let {
                    when(player){
                        is Player.Creator->{
                            if (it.joiner.isNotEmpty()&&it.joiner.isNotBlank()){

                            }
                        }
                        is Player.Joiner->{

                        }
                    }

                }


            })


//            flipViewBindingFront.answerTextView.text = gameRoom.creator.
//            if (gameRoom.creator.isEmpty() || gameRoom.creator.isBlank()){
//                //joiner
//                roomStatus = RoomStatus.Joiner
//            }else{
//                //creator
//                roomStatus = RoomStatus.Creator
//            }
//
//
//            when(roomStatus){
//               is RoomStatus.Creator ->{
//                   roomStatusTextView.text =
//               }
//               is RoomStatus.Joiner -> {
//
//               }
//
//            }


        }
    }

    private fun checkIsCreator(joiner: String): Boolean {
        return joiner.isEmpty()||joiner.isBlank()

    }

}