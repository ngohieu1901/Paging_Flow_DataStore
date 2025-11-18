package com.hieunt.base.base

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<M : Any, VH : BaseViewHolder<M, *>> : RecyclerView.Adapter<VH>() {
    val currentList: List<M> get() = differ.currentList

    protected abstract fun createViewHolder(viewType: Int, parent: ViewGroup): VH

    protected abstract fun layoutResource(position: Int): Int

    protected abstract fun areItemsTheSame(oldItem: M, newItem: M): Boolean

    protected abstract fun areContentsTheSame(oldItem: M, newItem: M): Boolean

    private val differ by lazy {
        AsyncListDiffer(this@BaseAdapter, object : DiffUtil.ItemCallback<M>() {
            override fun areItemsTheSame(oldItem: M, newItem: M): Boolean =
                this@BaseAdapter.areItemsTheSame(oldItem, newItem)

            override fun areContentsTheSame(oldItem: M, newItem: M): Boolean =
                this@BaseAdapter.areContentsTheSame(oldItem, newItem)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        createViewHolder(viewType, parent)

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bindData(differ.currentList[position])
    }

    override fun getItemViewType(position: Int): Int = layoutResource(position)

    override fun getItemCount(): Int = differ.currentList.size

    fun submitList(newList: List<M>) = differ.submitList(newList)


    fun submitList(newList: List<M>, onCommitted: () -> Unit) {
        differ.submitList(newList) {
            onCommitted()
        }
    }
}
