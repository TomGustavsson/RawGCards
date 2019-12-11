package com.tomgu.rawgcards.main.ui


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.tomgu.rawgcards.AppApplication

import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.main.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class GameListFragment : Fragment() {

    private lateinit var recyclerAdapter: RecyclerAdapter

    @Inject
    lateinit var vmFactory : MainViewModel.MainViewModelFactory

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        //Dagger2 Skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[MainViewModel::class.java]

        initRecyclerView()
        viewModel.getRoomItems()

        viewModel.getLiveDataRoom().observe(this, Observer {
            Log.d("blabla", it.toString())
            recyclerAdapter.submitGamesList(it)
            recyclerAdapter.notifyDataSetChanged()
        })

    }

    private fun initRecyclerView(){
        game_list_recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = RecyclerAdapter()
        game_list_recyclerview.adapter = recyclerAdapter
    }



}
