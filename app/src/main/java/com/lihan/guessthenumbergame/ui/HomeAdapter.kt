package com.lihan.guessthenumbergame.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom

class HomeAdapter(var gameRooms: MutableList<GameRoom>) : RecyclerView.Adapter<HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
       return HomeViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.home_gameroom_item,parent,false)
       )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bindTo(gameRooms[position])
    }

    override fun getItemCount() = gameRooms.size
}