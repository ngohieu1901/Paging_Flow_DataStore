package com.hieunt.base.presentations.feature.main

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hieunt.base.databinding.ItemFixtureLoadStateBinding
import com.hieunt.base.widget.layoutInflate

class FixtureLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<FixtureLoadStateAdapter.FixtureLoadStateVH>() {
    inner class FixtureLoadStateVH(
        private val binding: ItemFixtureLoadStateBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvRetry.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            binding.cpiLoading.isVisible = loadState is LoadState.Loading
            binding.tvRetry.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: FixtureLoadStateVH, loadState: LoadState) = holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FixtureLoadStateVH =
        FixtureLoadStateVH(ItemFixtureLoadStateBinding.inflate(parent.layoutInflate(), parent, false), retry)
}