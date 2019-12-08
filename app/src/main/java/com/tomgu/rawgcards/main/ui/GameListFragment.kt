package com.tomgu.rawgcards.main.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.MainViewModel
import com.tomgu.rawgcards.main.RecyclerAdapter
import com.tomgu.rawgcards.main.api.Game
import kotlinx.android.synthetic.main.fragment_game_list.*

/**
 * A simple [Fragment] subclass.
 */
class GameListFragment : Fragment() {

    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var viewModel : MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var gameList = mutableListOf<Game>()
        var cod = Game("cod", "Call of Duty", "4.5", "https://i.pinimg.com/originals/83/1a/23/831a23859314f537d8da00ab11ec1345.jpg")

        gameList.add(cod)
        initRecyclerView()

        recyclerAdapter.submitGamesList(gameList)
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView(){
        game_list_recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = RecyclerAdapter()
        game_list_recyclerview.adapter = recyclerAdapter
    }


}
