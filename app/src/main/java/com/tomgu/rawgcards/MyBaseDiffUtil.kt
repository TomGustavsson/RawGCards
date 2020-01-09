package com.tomgu.rawgcards

import androidx.recyclerview.widget.DiffUtil

class MyBaseDiffUtil<O,N>(private val oldList: List<O>, private val newList: List<N>): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition] == newList[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition]!!.equals(newList[newItemPosition]))
    }
}