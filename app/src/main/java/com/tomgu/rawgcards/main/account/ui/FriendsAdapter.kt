package com.tomgu.rawgcards.main.account.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.account.Account

class FriendsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var friendList: List<Account> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FriendsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.friend_list_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
       return friendList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FriendsViewHolder -> {
                holder.bind(friendList.get(position))
            }
        }
    }

    fun submitFriendList(accountFriends: MutableList<Account>){
        friendList = accountFriends
    }

    class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.circleImageFriendList)
        val text = itemView.findViewById<TextView>(R.id.friendEmailTextView)

        fun bind(account: Account){
            Picasso.get().load(account.photo).into(image)
            text.setText(account.email)

        }
    }
}