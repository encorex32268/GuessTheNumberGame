package com.lihan.guessthenumbergame.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(
    val homeRepository: HomeRepository
) : ViewModel() {

    fun getGameRooms() : MutableLiveData<MutableList<GameRoom>?> {
        return homeRepository.gameRooms
    }
}