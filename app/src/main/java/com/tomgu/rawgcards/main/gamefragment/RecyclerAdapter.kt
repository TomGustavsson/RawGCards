package com.tomgu.rawgcards.main.gamefragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.api.Game
import kotlinx.android.synthetic.main.game_list_item.view.*

class RecyclerAdapter(val onClickListener: OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var allGames : List<Game> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.game_list_item, parent, false
            ), onClickListener
        )
    }

    override fun getItemCount(): Int {
        return allGames.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
            is ViewHolder -> {
                holder.bind(allGames.get(position))
            }
        }
    }

    fun submitGamesList(listOfGames: List<Game>){
        allGames = listOfGames
    }

    class ViewHolder(itemView: View, val onClickListener: OnClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val gameImage = itemView.game_list_image
        val gameTitle = itemView.game_list_title
        val gameRating = itemView.game_list_rating

        fun bind(game : Game){
            gameTitle.setText(game.name)
            gameRating.setText(game.rating)
            Picasso.get().load(game.background_image).into(gameImage)

            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onClickListener.onClick(adapterPosition)
        }

    }

    interface OnClickListener{
        fun onClick(index: Int)
    }

}