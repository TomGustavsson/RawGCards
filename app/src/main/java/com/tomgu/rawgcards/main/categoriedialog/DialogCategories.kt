package com.tomgu.rawgcards.main.categoriedialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.categoriedialog.Categorie
import com.tomgu.rawgcards.main.categoriedialog.CategoriesRecyclerAdapter
import com.tomgu.rawgcards.main.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_game_list.*

class DialogCategories: DialogFragment() {

    lateinit var categoriesRecyclerAdapter: CategoriesRecyclerAdapter
    var allCategories: MutableList<Categorie> = mutableListOf()

    lateinit var categorieRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_categories_list, container, false)

        categorieRecycler = view.findViewById(R.id.categoriesRecyclerView)

        initRecyclerView()

        return view
    }

    private fun initRecyclerView() {

        categorieRecycler.layoutManager = LinearLayoutManager(activity)
        categoriesRecyclerAdapter = CategoriesRecyclerAdapter()
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(categorieRecycler)
        categorieRecycler.adapter = categoriesRecyclerAdapter
    }

    //Swipe to delete items
    val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //game categorie selected
            var categorie = viewHolder.adapterPosition

            var mainActivity: MainActivity = activity as MainActivity
            dismiss()
            mainActivity.changeSwitch(categoriesRecyclerAdapter.getItem(categorie))


        }

    }
}