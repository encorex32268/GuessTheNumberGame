package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.Constants
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemBackBinding
import com.lihan.guessthenumbergame.databinding.NumberCardItemFrontBinding
import com.lihan.guessthenumbergame.log
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding : FragmentGameBinding
    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()
    private lateinit var mGameRoom : GameRoom

    sealed class Player(var answerPlayer : Int){
        data class Creator(var answer:Int) : Player(answer)
        data class Joiner(var answer:Int) : Player(answer)
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
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            args.gameRoom.apply {
                if (checkIsCreator(this.joiner)){
                    roomStatusTextView.text = Constants.CREATE_WAITING_FOR_JOINER
                    player = Player.Creator(this.creatorAnswer)
                }else{
                    roomStatusTextView.text = Constants.JOINER_CREATOR_TURN
                    player = Player.Joiner(this.joinerAnswer)
                }
                mGameRoom = this
            }
            viewModel.getRoomStatus(mGameRoom.roomFullId).observe(viewLifecycleOwner,{
                when(player){
                    is Player.Creator->{
                        when(it.status){
                            Status.RoomCreated.name ->{ roomIDtextView.text = mGameRoom.id.toString() }
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
                                roomIDtextView.text = mGameRoom.id.toString()
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
            includechoicenumberview.apply {
                val numbersTextViewID = arrayListOf(
                number1TextView, number2TextView, number3TextView, number4TextView)

                guessNumberSendButton2.setOnClickListener {
                    val numbers = arrayListOf<Int>()
                    numbersTextViewID.forEach {
                        it.text.toString().apply{
                            try {
                                numbers.add(this.toInt())
                            }catch (e : NumberFormatException){
                                return@setOnClickListener
                            }

                        }
                    }
                    if(numbers.size != 4){
                        return@setOnClickListener
                    }else{
                        toCompare(numbers)
                        gameScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    cleanNumbersTextView(numbersTextViewID)
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
            numbercarditemfront.answerTextView.text = player.answerPlayer.toString()
        }

    }


    private fun cleanNumbersTextView(textViews : ArrayList<TextView>){
        textViews.forEach { textView ->
            textView.setOnClickListener {
                textView.text = ""
            }
            textView.text = ""
        }
    }

    private fun toCompare(numbers: ArrayList<Int>) {
        val otherAnswers = otherSideAnswer.toString().toCharArray()
        var resultA = 0
        var resultB = 0
        var numbersString = ""
        numbers.forEach {
            numbersString+=it.toString()
        }
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
        val resultMessage = "$numbersString -> ${resultA}A${resultB}B"
        binding.gameOutput.append("${resultMessage}\n")
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
                            log("Creator ${mGameRoom.toString()}")
                            it.joinerAnswer
                        }
                        is Player.Joiner->{
                            log("Joiner ${mGameRoom.toString()}")
                            it.creatorAnswer
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {} }
        )
    }


}