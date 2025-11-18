package com.hieunt.base.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hieunt.base.widget.tap

open class BaseViewHolder<M : Any, VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    protected val context: Context by lazy { binding.root.context }

    @CallSuper
    open fun bindData(data: M) {
        binding.root.tap {
            onItemClickListener(data)
        }
    }

    @CallSuper
    open fun onItemClickListener(data: M) = Unit
}
