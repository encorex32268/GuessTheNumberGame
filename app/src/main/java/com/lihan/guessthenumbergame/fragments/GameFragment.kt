package com.lihan.guessthenumbergame.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lihan.guessthenumbergame.*
import com.lihan.guessthenumbergame.databinding.FragmentGameBinding
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.viewmodel.GameViewModel
import java.lang.NumberFormatException
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.lihan.guessthenumbergame.repositories.AlertRoomFactory
import com.lihan.guessthenumbergame.repositories.InputNumberCheckerUtils
import com.lihan.guessthenumbergame.status.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class GameFragment : Fragment(R.layout.fragment_game),GameRoomStatusListener {

    private lateinit var binding : FragmentGameBinding
    private val args : GameFragmentArgs by navArgs()
    private val viewModel : GameViewModel by viewModels()

    private lateinit var mGameRoom : GameRoom
    private lateinit var mRoomStatus: RoomStatus

    private var otherSideAnswer = ""
    private var guessCounter = 0
    private var isCreator = false
    private lateinit var alertRoomFactory: AlertRoomFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alertRoomFactory = AlertRoomFactory(requireContext())
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpOnBackPressedCallBack()
        getArgumentsValue()
        setUpViewModel()
        viewModel.getGameRoom(mGameRoom.roomFullId)
        viewModel.getRoomStatus(mGameRoom.roomFullId)
        binding.apply {
            includechoicenumberview.apply {
                bindingViewInit()
                val numbersTextViewID = getNumbersTextViewID()
                guessNumberSendButton2.setOnClickListener {
                    val numbers = arrayListOf<Int>()
                    var numberString = ""
                    numbersTextViewID.forEach {
                        numberString += it.text.toString()
                        it.text.toString().apply{
                            try {
                                numbers.add(this.toInt())
                            }catch (e : NumberFormatException){
                                return@setOnClickListener
                            }
                        }
                    }
                    if(numbers.size != 4){ return@setOnClickListener }
                    else if (!InputNumberCheckerUtils.isCurrentNumber(numberString)){return@setOnClickListener}
                    else{
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

    private fun setUpViewModel() {
        lifecycleScope.apply {
            launchWhenStarted { wCreateRoom() }
            launchWhenStarted { wJoinerIntoRoom() }
            launchWhenStarted { wGameRoom() }
            launchWhenStarted  { wRoomStatus() }
            launchWhenStarted { wRemoveGameRoom() }
            launchWhenStarted { wRemoveJoiner() }
        }

    }

    private fun handleRoomStatus(roomStatus: RoomStatus) {
        if (isCreator){
            when(roomStatus.status){
                Status.RoomCreated.name->{
                    upDateStatusTextView(Status.RoomCreated.name)
                }
                Status.StartGame.name->{
                    upDateStatusTextView(Status.StartGame.name)
                }
                Status.CreatorEndGame.name->{
                    upDateStatusTextView(getString(R.string.status_creator_end_game))
                }
                Status.JoinerEndGame.name->{
                    upDateStatusTextView(getString(R.string.status_partner_is_end_game))
                }
                Status.EndGame.name->{
                    upDateStatusTextView(getString(R.string.status_partner_used,roomStatus.joinersGuess))
                }
                Status.CreatorExitGame.name->{
                    viewModel.removeGameRoomAndRoomStatus(roomStatus.roomFullID)
                }
                Status.JoinerExitGame.name->{
                    upDateStatusTextView(Status.RoomCreated.name)
                    viewModel.removeJoinerFromGameRoom(mGameRoom)
                }
            }
        }else{
            when(roomStatus.status){
                Status.RoomCreated.name->{ }
                Status.StartGame.name->{
                    upDateStatusTextView(Status.StartGame.name)
                }
                Status.CreatorEndGame.name->{
                    upDateStatusTextView(getString(R.string.status_partner_is_end_game))
                }
                Status.JoinerEndGame.name->{
                    upDateStatusTextView(getString(R.string.status_joiner_end_game))
                }
                Status.EndGame.name->{
                    upDateStatusTextView(getString(R.string.status_partner_used,roomStatus.creatorGuess))
                }
                Status.CreatorExitGame.name->{
                    findNavController().popBackStack()
                }
                Status.JoinerExitGame.name->{
                    findNavController().popBackStack()
                }
            }
        }

    }

    private fun getArgumentsValue() {
        args.gameRoom.apply {
            isCreator = checkIsCreator(this.joiner)
            mGameRoom = this
        }
        if (isCreator){
            viewModel.createGameRoom(mGameRoom)
        }else{
            viewModel.joinerIntoTheRoom(mGameRoom)
        }
    }

    private fun setUpOnBackPressedCallBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Handle the back button event
                    exitEvent()
                }
                fun exitEvent() {
                    if (isCreator) {
                        mRoomStatus.status = Status.CreatorExitGame.name
                    } else {
                        if (mRoomStatus.status == Status.EndGame.name) {
                            mRoomStatus.status = Status.CreatorExitGame.name
                        } else {
                            mRoomStatus.status = Status.JoinerExitGame.name
                        }
                    }
                    viewModel.setRoomStatus(mRoomStatus)
                }

            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
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

    override suspend fun wCreateRoom() {
        viewModel.createRoomsUIStatus.collect {
            when(it){
                is HomeUIStatus.Loading->{
                    binding.gameProgressBar.isVisible = true
                }
                is HomeUIStatus.Success->{
                    binding.gameProgressBar.isVisible = false
                    viewModel.setRoomStatus(mRoomStatus.apply {
                        status = Status.RoomCreated.name
                    })
                }
                is HomeUIStatus.Error->{
                    binding.gameProgressBar.isVisible = false
                    Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    override suspend fun wGameRoom() {
        viewModel.gameRoom.collect {
            when(it){
                is GameUIStatus.Loading->{}
                is GameUIStatus.Success<*>->{
                    mGameRoom = it.data  as GameRoom
                    binding.roomIDtextView.text = mGameRoom.id.toString()
                    otherSideAnswer = if (isCreator){
                        mGameRoom.joinerAnswer
                    }else{
                        mGameRoom.creatorAnswer
                    }
                }
                is GameUIStatus.Error->{

                }
                else -> Unit
            }

        }
    }

    override suspend fun wRoomStatus() {
        viewModel.roomStatus.collect {
            when(it){
                is GameUIStatus.Loading->{}
                is GameUIStatus.Success<*>->{
                    mRoomStatus = it.data as RoomStatus
                    handleRoomStatus(mRoomStatus)
                }
                is GameUIStatus.Error->{

                }
                else -> Unit
            }
        }
    }

    override suspend fun wRemoveGameRoom() {
        viewModel.removeGameRoom.collect {
            when(it){
                is GameRemoveUIStatus.Loading->{ }
                is GameRemoveUIStatus.Empty->{ }
                is GameRemoveUIStatus.Success->{
                    findNavController().popBackStack()
                }
                is GameRemoveUIStatus.Error->{ }

            }
        }
    }

    override suspend fun wRemoveJoiner() {
        viewModel.removeJoinerFromGameRoomStatus.collect {
            when(it){
                is UploadUIStatus.Loading->{}
                is UploadUIStatus.Empty->{}
                is UploadUIStatus.Success->{
                    viewModel.setRoomStatus(mRoomStatus.apply {
                        status = Status.RoomCreated.name
                    })
                    viewModel.setGameRoom(mGameRoom.apply{
                        joinerAnswer = ""
                        joiner = ""
                    })
                }
                is UploadUIStatus.Error->{}
            }
        }
    }

    override suspend fun wJoinerIntoRoom() {
        viewModel.joinerIntoTheRoom.collect {
            when (it) {
                is HomeUIStatus.Loading -> {
                    binding.gameProgressBar.isVisible = true
                }
                is HomeUIStatus.Success -> {
                    binding.gameProgressBar.isVisible = false
                    viewModel.setRoomStatus(mRoomStatus.apply {
                        status = Status.StartGame.name
                    })
                }
                is HomeUIStatus.Error -> {
                    binding.gameProgressBar.isVisible = false
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }


}