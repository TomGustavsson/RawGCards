package com.tomgu.rawgcards.main.gamefragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
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
import com.tomgu.rawgcards.main.gameinfofrag.GameInfoFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_game_list.*
import kotlinx.android.synthetic.main.fragment_game_list.view.*
import javax.inject.Inject

class GameListFragment : Fragment() {

    private lateinit var recyclerAdapter: MyBaseAdapter<Game, GameListItemBinding>

    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var gameInfoFragment: GameInfoFragment

    lateinit var viewModel: GameListViewModel
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_list, container, false)

        //Dagger2 Skit
        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[GameListViewModel::class.java]

        recyclerView = view.game_list_recyclerview
        initRecyclerView()
        viewModel.getRoomItems()

        viewModel.getLiveDataRoom().observe(viewLifecycleOwner, Observer {

            Observable.just(it)
                .map{Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(recyclerAdapter.listItems, it)))
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    recyclerAdapter.listItems = it.first
                    it.second.dispatchUpdatesTo(recyclerAdapter)

                },{
                    Log.d("tgiw", it.toString())
                })
        })

        return view
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerAdapter = object : MyBaseAdapter<Game, GameListItemBinding>(){
            override fun getLayoutResId(): Int {
               return R.layout.game_list_item
            }
            override fun onBindData(model: Game, dataBinding: GameListItemBinding) {
                dataBinding.game = model
                dataBinding.gameListImage.transitionName = "image_transition_" + model.slug
                dataBinding.gameListRoot.setOnClickListener {

                    gameInfoFragment = GameInfoFragment.newInstance(model.slug,dataBinding.gameListImage.transitionName, model, "NoFriend")

                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .addSharedElement(dataBinding.gameListImage, dataBinding.gameListImage.transitionName)
                        .replace(R.id.frame_layout, gameInfoFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView)
        recyclerView.adapter = recyclerAdapter
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
