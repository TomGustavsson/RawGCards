package com.tomgu.rawgcards.main.gameinfofrag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil
import com.tomgu.rawgcards.R
import io.reactivex.Observable
import com.tomgu.rawgcards.databinding.FriendListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.main.MyBaseAdapter
import com.tomgu.rawgcards.main.account.Account
import com.tomgu.rawgcards.main.account.ui.AccountDialogViewModel
import com.tomgu.rawgcards.main.account.ui.FriendFragment
import com.tomgu.rawgcards.main.api.Game
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BottomSheetDialog : BottomSheetDialogFragment() {


    @Inject
    lateinit var vmFactory : AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var friendsAdapter: MyBaseAdapter<Account, FriendListItemBinding>
    lateinit var bottomSheetRecyclerView: RecyclerView

    var game: Game? = null
    lateinit var state: String
    lateinit var share: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        state = arguments?.getString(STATE_ARGUMENT)!!
        share = arguments?.getString(SHARE_ARGUMENT)!!

        if(share == "SHARE"){
            game = (arguments?.getSerializable(GAME_ARGUMENT) as Game?)!!

        }

        bottomSheetRecyclerView = view.findViewById(R.id.bottomSheetRecyclerView)
        initRecyclerView()

        when(state){
            "FRIENDS"-> {
                viewModel.getFriends()
                viewModel.getFriendsLiveData().observe(this, Observer {
                    Observable.just(it)
                        .map {
                            Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(friendsAdapter.listItems, it)))
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            friendsAdapter.listItems = it.first
                            it.second.dispatchUpdatesTo(friendsAdapter)

                        }, {
                            Log.d("tgiw", it.toString())
                        })
                })
            }
            "USERS" -> {
                viewModel.getAllUsers()
                viewModel.getUsersLiveData().observe(this, Observer {
                    Observable.just(it)
                        .map { Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(friendsAdapter.listItems, it)))
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            friendsAdapter.listItems = it.first
                            it.second.dispatchUpdatesTo(friendsAdapter)

                        },{
                            Log.d("tgiw", it.toString())
                        })
                })
            }
            "REQUESTS" -> {
                viewModel.getAllFriendRequests()
                viewModel.getFriendRequestLiveData().observe(this, Observer {
                    Observable.just(it)
                        .map { Pair(it, DiffUtil.calculateDiff(MyBaseDiffUtil(friendsAdapter.listItems, it)))
                        }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            friendsAdapter.listItems = it.first
                            it.second.dispatchUpdatesTo(friendsAdapter)
                        }, {
                            Log.d("tgiw", it.toString())
                        })
                })

            }

        }

        return view
    }

    private fun initRecyclerView(){
        bottomSheetRecyclerView.layoutManager = LinearLayoutManager(activity)
        friendsAdapter = object : MyBaseAdapter<Account, FriendListItemBinding>() {
            override fun getLayoutResId(): Int {
                return R.layout.friend_list_item
            }
            override fun onBindData(model: Account, dataBinding: FriendListItemBinding) {
                dataBinding.account = model
                dataBinding.root.setOnClickListener {
                    if(share == "SHARE"){
                        dismiss()
                        viewModel.shareGame(game!!, model.uid!!)
                    } else {
                        dismiss()
                        val friendFragment = FriendFragment(model)
                        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                        friendFragment.tag
                        fragmentTransaction.replace(R.id.frame_layout, friendFragment)
                        fragmentTransaction.addToBackStack("FRIEND_FRAGMENT")
                        fragmentTransaction.commit()
                    }

                }
            }
        }
        bottomSheetRecyclerView.adapter = friendsAdapter
    }

    companion object{
        private const val GAME_ARGUMENT = "game"
        private const val STATE_ARGUMENT = "state"
        private const val SHARE_ARGUMENT = "share"

        fun newInstance(game : Game?, state : String, share : String) : BottomSheetDialog {
            val bottomSheetDialog = BottomSheetDialog()

            val arguments = Bundle()
            arguments.putString(STATE_ARGUMENT, state)
            arguments.putSerializable(GAME_ARGUMENT, game)
            arguments.putString(SHARE_ARGUMENT, share)

            bottomSheetDialog.arguments = arguments


            return bottomSheetDialog
        }
    }

}