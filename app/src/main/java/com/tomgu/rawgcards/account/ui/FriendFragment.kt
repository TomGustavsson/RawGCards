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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.FriendFragmentLayoutBinding
import com.tomgu.rawgcards.databinding.GameListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.MyBaseAdapter
import com.tomgu.rawgcards.account.Account
import com.tomgu.rawgcards.api.CompleteGame
import com.tomgu.rawgcards.gameinfofrag.GameInfoFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class FriendFragment(val friend: Account): Fragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var recyclerView : RecyclerView
    lateinit var gameInfoFragment: GameInfoFragment

    lateinit var adapter: MyBaseAdapter<CompleteGame, GameListItemBinding>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var binding : FriendFragmentLayoutBinding = FriendFragmentLayoutBinding.inflate(LayoutInflater.from(context))

        binding.lifecycleOwner = viewLifecycleOwner

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        recyclerView = binding.root.findViewById(R.id.sharedGamesRecyclerView)
        initRecyclerView()
        binding.account = friend
        binding.viewModel = viewModel

        viewModel.friendState(friend.uid!!)
        viewModel.isFriend().observe(this, Observer {
            if(it == true){
                setFriendsSharedGames()
            }
        })

        binding.acceptButton.setOnClickListener{
            viewModel.acceptFriendRequest(friend.uid)

        }

        binding.declineButton.setOnClickListener{
            viewModel.declineFriendRequest(friend.uid)
        }

        binding.addFriendImage.setOnClickListener{
            viewModel.addFriend(friend.uid)
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
        recyclerView.adapter = adapter

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
}