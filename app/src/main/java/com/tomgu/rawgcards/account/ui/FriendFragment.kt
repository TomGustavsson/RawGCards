package com.tomgu.rawgcards.account.ui


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
import com.tomgu.rawgcards.databinding.FriendFragmentLayoutBinding
import com.tomgu.rawgcards.databinding.GameListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.MyBaseAdapter
import com.tomgu.rawgcards.account.models.Account
import com.tomgu.rawgcards.api.CompleteGame
import com.tomgu.rawgcards.gameinfofrag.GameInfoFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class FriendFragment: Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var recyclerView : RecyclerView
    lateinit var gameInfoFragment: GameInfoFragment
    lateinit var friend : Account

    lateinit var adapter: MyBaseAdapter<CompleteGame, GameListItemBinding>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FriendFragmentLayoutBinding = FriendFragmentLayoutBinding.inflate(LayoutInflater.from(context))

        binding.lifecycleOwner = viewLifecycleOwner

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        friend = arguments?.getSerializable(FRIEND_ARG) as Account

        recyclerView = binding.root.findViewById(R.id.sharedGamesRecyclerView)
        initRecyclerView()
        binding.account = friend
        binding.viewModel = viewModel

        viewModel.friendState(friend.uid!!)
        viewModel.getFriendStateLiveData().observe(viewLifecycleOwner, Observer {
            if(it == FriendState.FRIEND)
                setFriendsSharedGames()
        })

        binding.acceptButton.setOnClickListener{
            viewModel.acceptFriendRequest(friend.uid!!)

        }

        binding.declineButton.setOnClickListener{
            viewModel.declineFriendRequest(friend.uid!!)
        }

        binding.addFriendImage.setOnClickListener{
            viewModel.addFriend(friend.uid!!)
        }


        return binding.root
    }

    fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(activity)
            adapter = object : MyBaseAdapter<CompleteGame, GameListItemBinding>(){
            override fun getLayoutResId(): Int {
                return R.layout.game_list_item
            }
            override fun onBindData(model: CompleteGame, dataBinding: GameListItemBinding) {
                dataBinding.game = model
                dataBinding.gameListImage.transitionName = "image_transition_" + model.slug
                dataBinding.gameListRoot.setOnClickListener {

                    gameInfoFragment = GameInfoFragment.newInstance(model.slug,dataBinding.gameListImage.transitionName, model, friend.uid!!)

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
        recyclerView.adapter = adapter
    }

    val itemTouchHelper = object :  ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val game = adapter.listItems.get(viewHolder.adapterPosition)
            viewModel.deleteSharedGame(game.slug, friend.uid!!)
        }

    }

    fun setFriendsSharedGames(){
        viewModel.getSharedGames(friend.uid!!)
        viewModel.getGamesLiveData().observe(this, Observer {
            Observable.just(it)
                .map{Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(adapter.listItems, it)))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.listItems = it.first
                    it.second.dispatchUpdatesTo(adapter)

                },{
                    Log.d("tgiw", it.toString())
                })
        })
    }

    companion object {

        private const val FRIEND_ARG = "friend"

        fun newInstance(friend: Account): FriendFragment {

            val friendFragment = FriendFragment()
            val arguments = Bundle()

            arguments.putSerializable(FRIEND_ARG, friend)

            friendFragment.arguments = arguments

            return friendFragment
        }
    }
}