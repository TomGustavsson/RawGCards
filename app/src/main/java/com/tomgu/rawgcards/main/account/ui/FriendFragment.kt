package com.tomgu.rawgcards.main.account.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FriendFragmentLayoutBinding
import com.tomgu.rawgcards.databinding.GameListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.MyBaseAdapter
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class FriendFragment(val friend: Account): Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var recyclerView : RecyclerView

    lateinit var adapter: MyBaseAdapter<Game, GameListItemBinding>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding : FriendFragmentLayoutBinding = FriendFragmentLayoutBinding.inflate(LayoutInflater.from(context))

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        recyclerView = binding.root.findViewById(R.id.sharedGamesRecyclerView)
        initRecyclerView()
        binding.account = friend

        viewModel.getCurrentAccount()
        viewModel.getCurrentAccountLiveData().observe(this, Observer {

            if(it.friendRequests!!.contains(friend.uid)){

                binding.acceptButton.visibility = View.VISIBLE
                binding.declineButton.visibility = View.VISIBLE

            } else if(it.friends!!.contains(friend.uid)){

                Log.d("Greken", "Already Friends")

            } else {

                binding.addFriendImage.visibility = View.VISIBLE

            }
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



        return binding.root
    }

    fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(activity)
            adapter = object : MyBaseAdapter<Game, GameListItemBinding>(){
            override fun getLayoutResId(): Int {
                return R.layout.game_list_item
            }
            override fun onBindData(model: Game, dataBinding: GameListItemBinding) {
                dataBinding.game = model
            }
        }
        recyclerView.adapter = adapter

    }
}