package com.lihan.guessthenumbergame.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.model.GameRoom
import com.lihan.guessthenumbergame.other.RoomClickListener

class HomeAdapter(var gameRooms: MutableList<GameRoom>) : RecyclerView.Adapter<HomeViewHolder>() {
    lateinit var roomClickListener : RoomClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
       return HomeViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.home_gameroom_item,parent,false)
       )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val gameRoom = gameRooms[position]
        holder.bindTo(gameRoom)
        holder.itemView.setOnClickListener {
            roomClickListener?.let {
                it.roomClick(gameRoom)
            }
        }
    }

    override fun getItemCount() = gameRooms.size
}