package com.lihan.guessthenumbergame.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lihan.guessthenumbergame.R
import com.lihan.guessthenumbergame.databinding.HomeGameroomItemBinding
import com.lihan.guessthenumbergame.model.GameRoom

class HomeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
{
    private val binding = HomeGameroomItemBinding.bind(itemView)

    fun bindTo(gameRoom: GameRoom){
        binding.apply {
            gameRoomNameTextView.text = gameRoom.id.toString()
            if (gameRoom.joiner.isEmpty()){
                gameRoomStatusTextView.setBackgroundResource(R.drawable.room_coonect_status_green)
            }else{
                gameRoomStatusTextView.setBackgroundResource(R.drawable.room_connect_status_red)
            }
        }
    }
}