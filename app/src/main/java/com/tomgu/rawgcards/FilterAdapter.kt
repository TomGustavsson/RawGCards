package com.tomgu.rawgcards

import android.widget.Filter
import android.widget.Filterable
import com.tomgu.rawgcards.account.models.Account
import com.tomgu.rawgcards.databinding.FriendListItemBinding

abstract class FilterAdapter : MyBaseAdapter<Account, FriendListItemBinding>(), Filterable {

    var listItemsFull = mutableListOf<Account>()

    override fun getFilter(): Filter {
        return appFilter
    }

    private val appFilter = object : Filter() {

        override fun performFiltering(p0: CharSequence?): FilterResults {

            val filteredList = mutableListOf<Account>()

            if(p0 == null || p0.isEmpty()){
                filteredList.addAll(listItemsFull)
            } else {
                val searchInput = p0.toString().toLowerCase().trim()
                listItemsFull.forEach {
                    if(it.email?.toLowerCase()?.contains(searchInput)!!){
                        filteredList.add(it)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                listItems = p1?.values as MutableList<Account>
                notifyDataSetChanged()

        }

    }
}