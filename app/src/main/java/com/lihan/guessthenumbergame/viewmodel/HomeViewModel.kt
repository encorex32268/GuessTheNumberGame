package com.lihan.guessthenumbergame.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.model.Status
import com.lihan.guessthenumbergame.other.Resources
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow


class HomeViewModel(application : Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val TAG = HomeViewModel::class.java.simpleName
    private val fireBaseRepository = FireBaseRepository(application)

    sealed class HomeUIStatus {
        object Loading : HomeUIStatus()
        object Empty : HomeUIStatus()
        data class Success(val data: ArrayList<GameRoom>) : HomeUIStatus()
        data class Error(val message: String) : HomeUIStatus()
    }

    private val _gameRoomsUIStatus = MutableStateFlow<HomeUIStatus>(HomeUIStatus.Empty)
    var gameRoomsUIStatus = _gameRoomsUIStatus

    private val _createRoomsUIStatus = MutableStateFlow<HomeUIStatus>(HomeUIStatus.Empty)
    var createRoomsUIStatus = _createRoomsUIStatus

    private val _joinerIntoTheRoomUIStatus = MutableStateFlow<HomeUIStatus>(HomeUIStatus.Empty)
    var joinerIntoTheRoom = _joinerIntoTheRoomUIStatus

    init {
        getGameRooms()
    }

    fun getGameRooms() {
        _gameRoomsUIStatus.value = HomeUIStatus.Loading
        val firebase = FirebaseDatabase.getInstance()
        val myRef = firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH))
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nowData = arrayListOf<GameRoom>()
                snapshot.children.forEach {
                    val gameRoom = it.getValue(GameRoom::class.java)
                    gameRoom?.let {
                        nowData.add(it)
                    }
                }
                _gameRoomsUIStatus.value = HomeUIStatus.Success(nowData)
            }

            override fun onCancelled(error: DatabaseError) {
                _gameRoomsUIStatus.value = HomeUIStatus.Error(error.message)
            }
        })
    }

    fun createGameRoom(mGameRoom: GameRoom) {
        _createRoomsUIStatus.value = HomeUIStatus.Loading
        val firebase = FirebaseDatabase.getInstance()
        val myRefGameRoom =
            firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMS_PATH))
                .child(mGameRoom.roomFullId)
        val myRefGameRoomStatus =
            firebase.getReference(context.getString(R.string.FIREBASE_GAMEROOMSTATUS_PATH))
                .child(mGameRoom.roomFullId)
        myRefGameRoom.setValue(mGameRoom).addOnCompleteListener {
            if (it.isSuccessful) {
                myRefGameRoomStatus.setValue(
                    RoomStatus(
                        mGameRoom.roomFullId,
                        Status.RoomCreated.name,
                        0, 0
                    )
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        _createRoomsUIStatus.value = HomeUIStatus.Success(arrayListOf(mGameRoom))

                    } else {
                        _createRoomsUIStatus.value = HomeUIStatus.Error(it.exception?.message.toString())
                    }
                }
            } else {
                _createRoomsUIStatus.value = HomeUIStatus.Error(it.exception?.message.toString())
            }
        }

    }

    fun joinerIntoTheRoom(gameRoom: GameRoom) {
        Log.d(TAG, "joinerIntoTheRoom: $gameRoom")
        _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Loading
            fireBaseRepository.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom).addOnCompleteListener {
                if(it.isSuccessful){
                    val roomStatus = RoomStatus(gameRoom.roomFullId, Status.StartGame.name,0,0)
                    fireBaseRepository.getGameRoomsStatusChildRef(gameRoom.roomFullId).setValue(roomStatus).addOnCompleteListener {
                        if(it.isSuccessful){
                            _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Success(arrayListOf(gameRoom))
                        }
                    }
                }else{
                    _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Error(it.exception?.message.toString())
                }
            }
    }
}