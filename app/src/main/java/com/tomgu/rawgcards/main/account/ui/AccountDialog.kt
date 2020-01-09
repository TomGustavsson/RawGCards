package com.tomgu.rawgcards.main.account.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
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
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AccountDialog: DialogFragment() {

    private val disposables = CompositeDisposable()

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

        viewModel.getCurrentAccountLiveData().observe(this, Observer {
            binding.account = it
        })

        viewModel.retrieveFriends().observe(this, Observer {
            val diffResult : DiffUtil.DiffResult = DiffUtil.calculateDiff(
                MyBaseDiffUtil(
                    friendsAdapter.listItems,
                    it
                )
            )
            friendsAdapter.listItems = it
            diffResult.dispatchUpdatesTo(friendsAdapter)
        })

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
            }
        }
        friendsRecyclerView.adapter = friendsAdapter
    }


}