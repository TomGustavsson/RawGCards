package com.tomgu.rawgcards.main.gamefragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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
        val oldList = allGames
        val diffResult : DiffUtil.DiffResult = DiffUtil.calculateDiff(
            GameItemDiffCallBack(
                oldList,
                listOfGames
            )
        )

        allGames = listOfGames
        diffResult.dispatchUpdatesTo(this)
    }

    fun getRecyclerList(): List<Game>{
        return allGames
    }

    class GameItemDiffCallBack(var oldList: List<Game>, var newList: List<Game>): DiffUtil.Callback(){
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldList.get(oldItemPosition).slug == newList.get(newItemPosition).slug)
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition))
        }


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