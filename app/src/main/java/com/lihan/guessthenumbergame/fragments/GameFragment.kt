package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.*
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException
import javax.inject.Inject
import androidx.activity.OnBackPressedCallback


@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

    private lateinit var binding : FragmentGameBinding
    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()

    private lateinit var mGameRoom : GameRoom
    private lateinit var mRoomStatus: RoomStatus

    private var otherSideAnswer = ""
    private var guessCounter = 0
    private var isCreator = false

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
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    log("Handle onBack Pressed")
                    exitEvent()

                }
                fun exitEvent(){
                    mRoomStatus.apply {
                        status = if (isCreator) Status.CreatorExitGame.name else Status.JoinerExitGame.name
                    }
                    fireBaseRepository.getGameRoomsStatusChildRef(mGameRoom.roomFullId).setValue(mRoomStatus).addOnCompleteListener {
                        findNavController().popBackStack()
                    }
                }

            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.apply {
            args.gameRoom.apply {
                isCreator = checkIsCreator(this.joiner)
                mGameRoom = this
            }
            viewModel.getGameRoom(mGameRoom.roomFullId).observe(viewLifecycleOwner,{ fbGameRoom ->
                mGameRoom = fbGameRoom.also {
                    roomIDtextView.text = it.id.toString()
                }
                otherSideAnswer = if (isCreator){
                    fbGameRoom.joinerAnswer
                }else{
                    fbGameRoom.creatorAnswer
                }


            })
            viewModel.getRoomStatus(mGameRoom.roomFullId).observe(viewLifecycleOwner,{ fbRoomStatus ->
                mRoomStatus = fbRoomStatus
                when(isCreator){
                    true ->{
                        when(fbRoomStatus.status){
                            Status.RoomCreated.name ->{ upDateStatusTextView(Status.RoomCreated.name) }
                            Status.StartGame.name ->{ upDateStatusTextView(Status.StartGame.name) }
                            Status.CreatorEndGame.name ->{ upDateStatusTextView("EndGame Wait Joiner") }
                            Status.JoinerEndGame.name ->{ upDateStatusTextView("Partner is EndGame") }
                            Status.EndGame.name ->{ upDateStatusTextView("Partner Used : ${fbRoomStatus.joinersGuess} Times") }
                            Status.CreatorExitGame.name->{ viewModel.removeGameRoomAndStatus(mGameRoom.roomFullId) }
                            Status.JoinerExitGame.name->{ upDateStatusTextView(Status.RoomCreated.name) }

                        }
                    }
                    false->{
                        when(fbRoomStatus.status){
                            Status.RoomCreated.name -> { }
                            Status.StartGame.name ->{ upDateStatusTextView(Status.StartGame.name) }
                            Status.CreatorEndGame.name ->{ upDateStatusTextView("Partner is EndGame")}
                            Status.JoinerEndGame.name ->{ upDateStatusTextView("EndGame Wait Creator")}
                            Status.EndGame.name ->{upDateStatusTextView("Partner Used : ${fbRoomStatus.creatorGuess} Times") }
                            Status.JoinerExitGame.name->{ viewModel.removeJoinerInGameRoom(mGameRoom)}
                            Status.CreatorExitGame.name->{findNavController().popBackStack()}
                        }
                    }
                }
            })
            includechoicenumberview.apply {
                bindingViewInit()
                val numbersTextViewID = getNumbersTextViewID()
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
            }
            numbercarditemfront.answerTextView.text = (if (isCreator) mGameRoom.creatorAnswer else mGameRoom.joinerAnswer).toString()
            binding.easyFlipView.flipTheView()
            numbercarditemfront.imageView.setOnClickListener {
                binding.easyFlipView.flipTheView()
            }
            numbercarditemback.imageView.setOnClickListener {
                binding.easyFlipView.flipTheView()
            }
        }

    }

    private fun upDateStatusTextView(displayText: String) {
        binding.roomStatusTextView.text = displayText
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
        guessCounter++
        val otherAnswers = otherSideAnswer.toCharArray()
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
        val resultMessage = "$guessCounter.$numbersString -> ${resultA}A${resultB}B"
        binding.gameOutput.append("${resultMessage}\n")
        if (resultA == 4){
            uploadResult()
        }
    }

    private fun uploadResult() {
        mRoomStatus.apply {
            when(true){
                isCreator && joinersGuess>0 ->{
                    creatorGuess = guessCounter
                    status = Status.EndGame.name
                }
                !isCreator && creatorGuess>0 ->{
                    joinersGuess = guessCounter
                    status = Status.EndGame.name
                }
                isCreator ->{
                    creatorGuess = guessCounter
                    status = Status.CreatorEndGame.name
                }
                !isCreator->{
                    joinersGuess = guessCounter
                    status = Status.JoinerEndGame.name
                }
            }
        }
        viewModel.setRoomStatus(mRoomStatus)
    }

    private fun checkIsCreator(joiner: String): Boolean {
        return joiner.isEmpty()||joiner.isBlank()
    }






}