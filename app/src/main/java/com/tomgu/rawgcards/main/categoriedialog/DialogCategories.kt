package com.tomgu.rawgcards.main.categoriedialog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils.getMonthString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.CategorieListItemBinding
import com.tomgu.rawgcards.main.MyBaseAdapter


class DialogCategories: DialogFragment() {

    lateinit var categoriesRecyclerAdapter: MyBaseAdapter<Categorie, CategorieListItemBinding>
    lateinit var categorieRecycler: RecyclerView

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_categories_list, container, false)

        categorieRecycler = view.findViewById(R.id.categoriesRecyclerView)

        initRecyclerView()

        return view
    }

    private fun initRecyclerView() {

        categorieRecycler.layoutManager = LinearLayoutManager(activity)
        categoriesRecyclerAdapter = object : MyBaseAdapter<Categorie, CategorieListItemBinding>(){
            override fun getLayoutResId(): Int {
                return R.layout.categorie_list_item
            }
            override fun onBindData(model: Categorie, dataBinding: CategorieListItemBinding) {
                dataBinding.categorie = model
                dataBinding.categorieRoot.setOnClickListener {
                    val i: Intent = Intent()
                        .putExtra("categorie", model.name)
                    targetFragment!!.onActivityResult(
                        targetRequestCode,
                        Activity.RESULT_OK,
                        i
                    )
                    dismiss()
                }
            }
        }
        categorieRecycler.adapter = categoriesRecyclerAdapter
        categoriesRecyclerAdapter.listItems = categories
    }


}