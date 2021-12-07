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
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.Constants
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.ChoicenumberViewBinding
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemBackBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemFrontBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.FireBaseRepository
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding : FragmentGameBinding
    private lateinit var flipViewBindingFront : NumberCardItemFrontBinding
    private lateinit var flipViewBindingBack : NumberCardItemBackBinding
    private lateinit var choicenumberViewBinding: ChoicenumberViewBinding

    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()

    private lateinit var mGameRoom : GameRoom

    sealed class Player{
        object Creator : Player()
        object Joiner : Player()
    }
    private lateinit var player: Player
    private var otherSideAnswer = 0

    @Inject
    lateinit var fireBaseRepository: FireBaseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        flipViewBindingFront = NumberCardItemFrontBinding.inflate(layoutInflater)
        flipViewBindingBack  = NumberCardItemBackBinding.inflate(layoutInflater)
        choicenumberViewBinding = ChoicenumberViewBinding.inflate(layoutInflater)
        binding = FragmentGameBinding.inflate(layoutInflater)
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
                            Status.RoomCreated.name ->{ roomIDtextView.text = mGameRoom.roomFullId }
                            Status.StartGame.name ->{
                                getGameRoomFromFireBase()
                                roomStatusTextView.text = "START"
                            }
                            Status.CreatorWin.name ->{ roomStatusTextView.text = "Win" }
                            Status.JoinerWin.name ->{ roomStatusTextView.text = "Loss" }
                            Status.EndGame.name ->{
                                roomStatusTextView.text = "End Game Uploading..."
                                uploadResult()
                            }

                        }
                    }
                    is Player.Joiner->{
                        when(it.status){
                            Status.RoomCreated.name -> { }
                            Status.StartGame.name ->{
                                roomIDtextView.text = mGameRoom.roomFullId
                                roomStatusTextView.text = "START"
                                getGameRoomFromFireBase()

                            }
                            Status.CreatorWin.name ->{ roomStatusTextView.text = "Loss" }
                            Status.JoinerWin.name ->{ roomStatusTextView.text = "Win" }
                            Status.EndGame.name ->{
                                roomStatusTextView.text = "End Game Uploading..."
                                uploadResult()
                            }
                        }
                    }
                }
            })

        }

        choicenumberViewBinding.apply {
            val numbersTextViewID = arrayListOf(
                number1TextView,number2TextView,number3TextView,number4TextView
            )
            numbersTextViewID.forEach {  textView ->
                textView.setOnClickListener {
                    textView.text = ""
                }
            }
            guessNumberSendButton2.setOnClickListener {
                val numbers = arrayListOf<Int>()
                numbersTextViewID.forEach {
                    if (it.text.isNullOrBlank() && it.text.isNullOrEmpty()){
                        numbers.add((it.text).toString().toInt())
                    }
                }
                if(numbers.size != 4){
                    return@setOnClickListener
                }else{
                    //send
                    toCompare(numbers)
                }
            }
            val choiceNumbersTextViewID = arrayListOf(
                choiceNum1TextView,choiceNum2TextView,choiceNum3TextView,choiceNum4TextView,choiceNum5TextView,
                choiceNum6TextView, choiceNum7TextView,choiceNum8TextView,choiceNum9TextView,choiceNum10TextView
            )
            choiceNumbersTextViewID.forEach { textView ->
                textView.setOnClickListener {
                    var isSet = false
                    numbersTextViewID.forEach {
                        if (!isSet && it.text.isEmpty()){
                            isSet = !isSet
                            it.text = textView.text.toString()
                        }
                    }
                }
            }


        }



    }

    private fun toCompare(numbers: ArrayList<Int>) {
        val otherAnswers = otherSideAnswer.toString().toCharArray()
        var resultA = 0
        var resultB = 0
        for (index in otherAnswers.indices) {
            val number1 = otherAnswers[index].toString().toInt()
            for (index2 in numbers.indices){
                val number2 = numbers[index2]
                when(true){
                    (number1 == number2)&& (index == index2)->{
                        resultA++
                    }
                    (number1 == number2)&&(index!=index2)->{
                        resultB++
                    }
                }
            }
        }
    }

    private fun uploadResult() {

    }

    private fun checkIsCreator(joiner: String): Boolean {
        return joiner.isEmpty()||joiner.isBlank()
    }


    private fun getGameRoomFromFireBase(){
        fireBaseRepository.getGameRoomsChildRef(mGameRoom.roomFullId)
            .addValueEventListener(object : ValueEventListener {
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