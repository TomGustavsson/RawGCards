package com.tomgu.rawgcards.main.gamefragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.gamedialog.GameInfoDialog
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class GameListFragment : Fragment(), RecyclerAdapter.OnClickListener {

    private lateinit var recyclerAdapter: RecyclerAdapter

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var gameInfoDialog: GameInfoDialog

    lateinit var viewModel: GameListViewModel
    lateinit var clickList : MutableList<Game>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //Dagger2 Skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[GameListViewModel::class.java]

        gameInfoDialog = GameInfoDialog()
        initRecyclerView()
        viewModel.getRoomItems()

        viewModel.getLiveDataRoom().observe(this, Observer {
            Log.d("blabla", it.toString())
            recyclerAdapter.submitGamesList(it)
            recyclerAdapter.notifyDataSetChanged()
            clickList = it
        })

    }

    private fun initRecyclerView(){
        game_list_recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter =
            RecyclerAdapter(this)
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(game_list_recyclerview)
        game_list_recyclerview.adapter = recyclerAdapter
    }

    //Click to fragment dialog
    override fun onClick(index: Int) {
        gameInfoDialog.show(fragmentManager!!,"gameInfoDialog")
        gameInfoDialog.setGameString(clickList.get(index).slug)
        Log.d("hihi", "Game is clicked: " + clickList.get(index))
    }

        //Swipe to delete items
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var game = clickList.get(viewHolder.adapterPosition)
            clickList.removeAt(viewHolder.adapterPosition)
            viewModel.deleteGame(game)
        }
    }


}
