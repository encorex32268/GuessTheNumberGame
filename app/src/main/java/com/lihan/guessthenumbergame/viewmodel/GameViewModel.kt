package com.lihan.guessthenumbergame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.model.RoomStatus
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.status.*
import kotlinx.coroutines.flow.MutableStateFlow

class GameViewModel(application: Application) : AndroidViewModel(application){

    private val firebase = FireBaseRepository(application)


    private val _gameRoom = MutableStateFlow<GameUIStatus>(GameUIStatus.Empty)
    var gameRoom = _gameRoom

    private val _roomStatus = MutableStateFlow<GameUIStatus>(GameUIStatus.Empty)
    var roomStatus = _roomStatus

    private val _removeGameRoom = MutableStateFlow<GameRemoveUIStatus>(GameRemoveUIStatus.Empty)
    var removeGameRoom = _removeGameRoom

    private val _setRoomStatusResult = MutableStateFlow<UploadUIStatus>(UploadUIStatus.Empty)
    var setRoomStatusResult = _setRoomStatusResult

    private val _setGameRoomResult = MutableStateFlow<UploadUIStatus>(UploadUIStatus.Empty)
    var setGameRoomResult = _setGameRoomResult

    private val _removeJoinerFromGameRoomStatus = MutableStateFlow<UploadUIStatus>(UploadUIStatus.Empty)
    var removeJoinerFromGameRoomStatus = _removeJoinerFromGameRoomStatus

    private val _createRoomsUIStatus = MutableStateFlow<HomeUIStatus>(HomeUIStatus.Empty)
    var createRoomsUIStatus = _createRoomsUIStatus

    private val _joinerIntoTheRoomUIStatus = MutableStateFlow<HomeUIStatus>(
        HomeUIStatus.Empty)
    var joinerIntoTheRoom = _joinerIntoTheRoomUIStatus
    
    
    fun getGameRoom(roomFullID: String) {
        _gameRoom.value = GameUIStatus.Loading
        firebase.getGameRoomsChildRef(roomFullID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(GameRoom::class.java)?.let {
                    _gameRoom.value = GameUIStatus.Success(it)
                    _gameRoom.value = GameUIStatus.Empty
                }
            }
            override fun onCancelled(error: DatabaseError) {
                _gameRoom.value = GameUIStatus.Error(error.message)
            }
        })
    }

    fun getRoomStatus(roomFullID : String){
        _roomStatus.value = GameUIStatus.Loading
        firebase.getGameRoomsStatusChildRef(roomFullID).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(RoomStatus::class.java)?.let {
                    _roomStatus.value = GameUIStatus.Success(it)
                    _roomStatus.value = GameUIStatus.Empty
                }
            }
            override fun onCancelled(error: DatabaseError) {
                _roomStatus.value = GameUIStatus.Error(error.message)
            }
        })
    }

    fun removeGameRoomAndRoomStatus(roomFullID: String){
        _removeGameRoom.value = GameRemoveUIStatus.Loading
        firebase.getGameRoomsChildRef(roomFullID).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                firebase.getGameRoomsStatusChildRef(roomFullID).removeValue().addOnCompleteListener {
                    if (it.isSuccessful){
                        _removeGameRoom.value = GameRemoveUIStatus.Success
                        _removeGameRoom.value = GameRemoveUIStatus.Empty
                    }else{
                        _removeGameRoom.value = GameRemoveUIStatus.Error(it.exception?.message.toString())
                    }
                }
            }else{
                _removeGameRoom.value = GameRemoveUIStatus.Error(it.exception?.message.toString())
            }
        }
    }

    fun setRoomStatus(roomStatus: RoomStatus) {
        _setRoomStatusResult.value = UploadUIStatus.Loading
        firebase.getGameRoomsStatusChildRef(roomStatus.roomFullID).setValue(roomStatus)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _setRoomStatusResult.value = UploadUIStatus.Success
                    _setRoomStatusResult.value = UploadUIStatus.Empty
                } else {
                    _setRoomStatusResult.value = UploadUIStatus.Error(it.exception?.message.toString())
                }
            }
    }
    fun setGameRoom(gameRoom: GameRoom) {
        _setGameRoomResult.value = UploadUIStatus.Loading
        firebase.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _setGameRoomResult.value = UploadUIStatus.Success
                    _setGameRoomResult.value = UploadUIStatus.Empty
                } else {
                    _setGameRoomResult.value =
                        UploadUIStatus.Error(it.exception?.message.toString())
                }
            }
    }

    fun removeJoinerFromGameRoom(gameRoom: GameRoom){
        _removeJoinerFromGameRoomStatus.value = UploadUIStatus.Loading
        firebase.getGameRoomsChildRef(gameRoom.roomFullId).setValue(gameRoom.apply {
            joiner = ""
            joinerAnswer = ""
        }).addOnCompleteListener {
            if (it.isSuccessful){
                _removeJoinerFromGameRoomStatus.value = UploadUIStatus.Success
                _removeJoinerFromGameRoomStatus.value = UploadUIStatus.Empty
            }else{
                _removeJoinerFromGameRoomStatus.value = UploadUIStatus.Error(it.exception?.message.toString())
            }
        }
    }


    fun createGameRoom(mGameRoom: GameRoom) {
        _createRoomsUIStatus.value = HomeUIStatus.Loading
        val myRefGameRoom = firebase.getGameRoomsChildRef(mGameRoom.roomFullId)
        val myRefGameRoomStatus = firebase.getGameRoomsStatusChildRef((mGameRoom.roomFullId))
        myRefGameRoom.setValue(mGameRoom).addOnSuccessListener {
            myRefGameRoomStatus.setValue(RoomStatus(
                mGameRoom.roomFullId,
                Status.RoomCreated.name,
                0, 0
            )).addOnSuccessListener {
                _createRoomsUIStatus.value = HomeUIStatus.Success(arrayListOf())
                _createRoomsUIStatus.value = HomeUIStatus.Empty
            }.addOnFailureListener {
                _createRoomsUIStatus.value = HomeUIStatus.Error(it.message.toString())

            }
        }.addOnFailureListener {
            _createRoomsUIStatus.value = HomeUIStatus.Error(it.message.toString())

        }


    }

    fun joinerIntoTheRoom(gameRoom: GameRoom) {
        val myRefGameRoom = firebase.getGameRoomsChildRef(gameRoom.roomFullId)
        val myRefGameRoomStatus = firebase.getGameRoomsStatusChildRef((gameRoom.roomFullId))

        _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Loading
        myRefGameRoom.setValue(gameRoom).addOnCompleteListener {
            if(it.isSuccessful){
                val roomStatus = RoomStatus(gameRoom.roomFullId, Status.StartGame.name,0,0)
                myRefGameRoomStatus.setValue(roomStatus).addOnCompleteListener {
                    if(it.isSuccessful){
                        _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Success(arrayListOf())
                        _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Empty
                    }
                }
            }else{
                _joinerIntoTheRoomUIStatus.value = HomeUIStatus.Error(it.exception?.message.toString())
            }
        }
    }





}