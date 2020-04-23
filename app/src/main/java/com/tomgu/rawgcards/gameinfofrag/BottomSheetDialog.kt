package com.tomgu.rawgcards.gameinfofrag

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tomgu.rawgcards.*
import io.reactivex.Observable
import com.tomgu.rawgcards.databinding.FriendListItemBinding
import com.tomgu.rawgcards.di.AppApplication
import com.tomgu.rawgcards.account.models.Account
import com.tomgu.rawgcards.account.ui.AccountDialogViewModel
import com.tomgu.rawgcards.account.ui.FriendFragment
import com.tomgu.rawgcards.account.ui.FriendState
import com.tomgu.rawgcards.api.CompleteGame
import com.tomgu.rawgcards.databinding.BottomSheetLayoutBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class BottomSheetDialog : BottomSheetDialogFragment() {


    @Inject
    lateinit var vmFactory: AppViewModelFactory
    lateinit var viewModel: AccountDialogViewModel

    lateinit var friendsAdapter: FilterAdapter
    lateinit var bottomSheetRecyclerView: RecyclerView

    var game: CompleteGame? = null
    lateinit var share: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: BottomSheetLayoutBinding =
            BottomSheetLayoutBinding.inflate(LayoutInflater.from(context))

        (activity?.applicationContext as AppApplication).appComponent().inject(this)
        viewModel = ViewModelProviders.of(this, vmFactory)[AccountDialogViewModel::class.java]

        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        share = arguments?.getString(SHARE_ARGUMENT)!!
        binding.lifecycleOwner = viewLifecycleOwner
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        if (share == "SHARE") {
            game = (arguments?.getSerializable(GAME_ARGUMENT) as CompleteGame?)!!
        }
        bottomSheetRecyclerView = binding.bottomSheetRecyclerView

        initRecyclerView()

        viewModel.getUsers(arguments?.getSerializable(STATE_ARGUMENT) as FriendState)
        viewModel.getUserLiveData().observe(viewLifecycleOwner, Observer {
            friendsAdapter.listItems = it
            friendsAdapter.listItemsFull = it as MutableList<Account>
            friendsAdapter.notifyDataSetChanged()
        })

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                val searchView = item.actionView as androidx.appcompat.widget.SearchView

                searchView.imeOptions = EditorInfo.IME_ACTION_DONE

                searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        friendsAdapter.filter.filter(newText)
                        return false
                    }

                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initRecyclerView() {
        bottomSheetRecyclerView.layoutManager = LinearLayoutManager(activity)
        friendsAdapter = object : FilterAdapter() {
            override fun getLayoutResId(): Int {
                return R.layout.friend_list_item
            }

            override fun onBindData(model: Account, dataBinding: FriendListItemBinding) {
                dataBinding.account = model
                dataBinding.root.setOnClickListener {
                    if (share == "SHARE") {
                        dismiss()
                        viewModel.shareGame(game!!, model.uid!!)
                    } else {
                        dismiss()
                        val friendFragment = FriendFragment.newInstance(model)
                        val fragmentTransaction: FragmentTransaction =
                            fragmentManager!!.beginTransaction()
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

        fun newInstance(game : CompleteGame?, state : FriendState, share : String) : BottomSheetDialog {
            val bottomSheetDialog = BottomSheetDialog()

            val arguments = Bundle()
            arguments.putSerializable(STATE_ARGUMENT, state)
            arguments.putSerializable(GAME_ARGUMENT, game)
            arguments.putString(SHARE_ARGUMENT, share)

            bottomSheetDialog.arguments = arguments


            return bottomSheetDialog
        }
    }

}