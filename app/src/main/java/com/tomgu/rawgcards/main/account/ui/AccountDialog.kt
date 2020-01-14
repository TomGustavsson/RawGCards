package com.tomgu.rawgcards.main.account.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.AppViewModelFactory
import com.tomgu.rawgcards.MyBaseDiffUtil
import com.tomgu.rawgcards.R
import com.tomgu.rawgcards.databinding.DialogAccountBinding
import com.tomgu.rawgcards.databinding.FriendListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.login.LoginActivity
import com.tomgu.rawgcards.main.MyBaseAdapter
import com.tomgu.rawgcards.main.account.Account
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AccountDialog: DialogFragment() {

    @Inject
    lateinit var vmFactory: AppViewModelFactory

    lateinit var viewModel: AccountDialogViewModel

    lateinit var friendsAdapter: MyBaseAdapter<Account, FriendListItemBinding>

    lateinit var signOutText: TextView
    lateinit var friendsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : DialogAccountBinding = DialogAccountBinding.inflate(LayoutInflater.from(context))

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        friendsRecyclerView= binding.root.findViewById(R.id.friendsRecyclerView) as RecyclerView
        initRecyclerView()

        signOutText = binding.root.findViewById(R.id.signOutTextView)

        viewModel.getCurrentAccount()
        friends()

        viewModel.getCurrentAccountLiveData().observe(this, Observer {
            binding.account = it
        })

        viewModel.getFriendsLiveData().observe(viewLifecycleOwner, Observer {

            Observable.just(it)
                .map{Pair(it,DiffUtil.calculateDiff(MyBaseDiffUtil(friendsAdapter.listItems, it)))
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    friendsAdapter.listItems = it.first
                    it.second.dispatchUpdatesTo(friendsAdapter)
                    Log.d("greken", it.toString())

                },{
                    Log.d("tgiw", it.toString())
                })
        })

        viewModel.getUsersLiveData().observe(this, Observer{
            Observable.just(it)
                .map{Pair(it,DiffUtil.calculateDiff(MyBaseDiffUtil(friendsAdapter.listItems, it)))
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

        binding.usersTextView.setOnClickListener {
            viewModel.getAllUsers()
        }

        binding.friendsTextView.setOnClickListener {
            friends()
        }

        signOutText.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }
    private fun initRecyclerView() {
        friendsRecyclerView.layoutManager = LinearLayoutManager(activity)
        friendsAdapter = object : MyBaseAdapter<Account, FriendListItemBinding>() {
                override fun getLayoutResId(): Int {
                    return R.layout.friend_list_item
                }
                override fun onBindData(model: Account, dataBinding: FriendListItemBinding) {
                    dataBinding.account = model
                    dataBinding.root.setOnClickListener {
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
        friendsRecyclerView.adapter = friendsAdapter
    }

    private fun friends(){
        viewModel.getFriends()
    }


}