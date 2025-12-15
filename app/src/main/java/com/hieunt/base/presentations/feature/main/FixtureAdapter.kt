package com.hieunt.base.presentations.feature.main

import android.view.ViewGroup
import com.hieunt.base.base.BaseAdapter
import com.hieunt.base.base.BaseViewHolder
import com.hieunt.base.databinding.ItemFixtureBinding
import com.hieunt.base.domain.model.FixtureDomain
import com.hieunt.base.widget.layoutInflate

class FixtureAdapter: BaseAdapter<FixtureDomain, FixtureAdapter.FixtureVH>() {
    inner class FixtureVH(binding: ItemFixtureBinding): BaseViewHolder<FixtureDomain, ItemFixtureBinding>(binding) {
        override fun bindData(data: FixtureDomain) {
            super.bindData(data)
            binding.apply {

            }
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): FixtureVH = FixtureVH(ItemFixtureBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = 0

    override fun areContentsTheSame(oldItem: FixtureDomain, newItem: FixtureDomain): Boolean = oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: FixtureDomain, newItem: FixtureDomain): Boolean = oldItem == newItem
}