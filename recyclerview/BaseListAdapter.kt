package com.example.recyclerviewbestpractice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T : Any, VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val itemClickListener: ((T) -> Unit)? = null,
    inline val bind: (item: T, binding: VB, position: Int) -> Unit,
) : ListAdapter<T, BaseListAdapter.BaseViewHolder<VB>>(BaseItemCallback<T>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {
//        bind(getItem(position), holder.binding, position)
        val item = getItem(position)
        bind(item, holder.binding, position)
        holder.binding.root.setOnClickListener {
            itemClickListener?.invoke(item)
        }
    }

    class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)
}

class BaseItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem.toString() == newItem.toString()
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
