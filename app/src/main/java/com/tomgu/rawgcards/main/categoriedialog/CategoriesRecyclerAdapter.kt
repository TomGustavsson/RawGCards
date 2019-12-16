package com.tomgu.rawgcards.main.categoriedialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tomgu.rawgcards.R

class CategoriesRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var categories : List<Categorie> = listOf(
        Categorie(
            "Action",
            R.drawable.action_icon
        ),
        Categorie(
            "Fighting",
            R.drawable.fighting_icon
        ),
        Categorie(
            "RPG",
            R.drawable.rpg_icon
        ),
        Categorie(
            "Racing",
            R.drawable.racing_icon
        ),
        Categorie(
            "Shooting",
            R.drawable.shooting_icon
        )
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.categorie_list_item, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CategoriesViewHolder -> {
                holder.bind(categories.get(position))
            }
        }

    }
    fun getItem(position : Int) : Categorie{
        return categories[position]
    }

    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image = itemView.findViewById<ImageView>(R.id.categorie_imageview)
        val text = itemView.findViewById<TextView>(R.id.categorie_textview)

        fun bind(categorie: Categorie){
            Picasso.get().load(categorie.image).into(image)
            text.setText(categorie.name)
        }
    }

}