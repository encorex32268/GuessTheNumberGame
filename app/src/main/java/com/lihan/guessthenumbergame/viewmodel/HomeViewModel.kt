package com.lihan.guessthenumbergame.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.repositories.FireBaseRepository
import com.lihan.guessthenumbergame.status.HomeUIStatus
import kotlinx.coroutines.flow.MutableStateFlow


class HomeViewModel(application : Application) : AndroidViewModel(application) {
    private val fireBaseRepository = FireBaseRepository(application)

    private val _gameRoomsUIStatus = MutableStateFlow<HomeUIStatus>(HomeUIStatus.Empty)
    var gameRoomsUIStatus = _gameRoomsUIStatus

    fun getGameRooms() {
        _gameRoomsUIStatus.value = HomeUIStatus.Loading
        val myRefGameRoom = fireBaseRepository.getGameRoomsRef()
        myRefGameRoom.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nowData = arrayListOf<GameRoom>()
                snapshot.children.forEach {
                    val gameRoom = it.getValue(GameRoom::class.java)
                    gameRoom?.let {
                        nowData.add(it)
                    }
                }
                _gameRoomsUIStatus.value = HomeUIStatus.Success(nowData)
                //TODO 不改沒辦法再次進入 GameRoom
                _gameRoomsUIStatus.value = HomeUIStatus.Empty
            }
            override fun onCancelled(error: DatabaseError) {
                _gameRoomsUIStatus.value = HomeUIStatus.Error(error.message)
            }
        })
    }

}