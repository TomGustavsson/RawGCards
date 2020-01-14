package com.tomgu.rawgcards.main.gamefragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.GameListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.MyBaseAdapter
import com.tomgu.rawgcards.main.api.Game
import com.tomgu.rawgcards.main.gamedialog.GameInfoDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_game_list.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class GameListFragment : Fragment() {

    private lateinit var recyclerAdapter: MyBaseAdapter<Game, GameListItemBinding>

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var gameInfoDialog: GameInfoDialog

    lateinit var viewModel: GameListViewModel

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

            Observable.just(it)
                .map{Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(recyclerAdapter.listItems, it)))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    recyclerAdapter.listItems = it.first
                    it.second.dispatchUpdatesTo(recyclerAdapter)

                },{
                    Log.d("tgiw", it.toString())
                })
        })

    }

    private fun initRecyclerView(){
        game_list_recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = object : MyBaseAdapter<Game, GameListItemBinding>(){
            override fun getLayoutResId(): Int {
               return R.layout.game_list_item
            }
            override fun onBindData(model: Game, dataBinding: GameListItemBinding) {
                dataBinding.game = model
                dataBinding.gameListRoot.setOnClickListener {
                    gameInfoDialog.show(fragmentManager!!,"gameInfoDialog")
                    gameInfoDialog.setGameString(model.slug)

                }
            }

        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(game_list_recyclerview)
        game_list_recyclerview.adapter = recyclerAdapter
    }

        //Swipe to delete items
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var game = recyclerAdapter.listItems.get(viewHolder.adapterPosition)
            viewModel.deleteGame(game)
        }
    }


}
