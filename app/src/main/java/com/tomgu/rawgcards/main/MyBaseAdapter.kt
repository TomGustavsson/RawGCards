package com.tomgu.rawgcards.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tomgu.rawgcards.MyBaseDiffUtil


abstract class MyBaseAdapter<T, D : ViewDataBinding> :
    RecyclerView.Adapter<MyBaseAdapter.MyViewHolder<D>>() {


    var listItems: List<T> = mutableListOf()

    abstract fun getLayoutResId(): Int

    abstract fun onBindData(model: T, dataBinding: D)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<D> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(layoutInflater, getLayoutResId(), parent, false) as D

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }


    override fun onBindViewHolder(holder: MyViewHolder<D>, position: Int) {
        onBindData(listItems[position], holder.binding)
    }

    class MyViewHolder<B : ViewDataBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

}