package com.hieunt.base.presentations.feature.screen_base.uninstall

import android.view.ViewGroup
import com.hieunt.base.R
import com.hieunt.base.base.BaseSyncDifferAdapter
import com.hieunt.base.base.BaseViewHolder
import com.hieunt.base.databinding.ItemAnswerBinding
import com.hieunt.base.domain.model.AnswerModel
import com.hieunt.base.widget.layoutInflate

class UninstallAdapter(private val onClick: (AnswerModel, position: Int) -> Unit): BaseSyncDifferAdapter<AnswerModel, UninstallAdapter.AnswerVH>() {
    inner class AnswerVH(binding: ItemAnswerBinding): BaseViewHolder<AnswerModel, ItemAnswerBinding>(binding){
        override fun bindData(data: AnswerModel) {
            super.bindData(data)
            binding.apply {
                tvName.text = context.getString(data.name)
                if (data.isSelected) {
                    rdSelect.setImageResource(R.drawable.rd_select_why)
                } else {
                    rdSelect.setImageResource(R.drawable.rd_un_select_why)
                }
            }
        }

        override fun onItemClickListener(data: AnswerModel) {
            super.onItemClickListener(data)
            onClick(data.copy(isSelected = true), bindingAdapterPosition)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): AnswerVH {
        return AnswerVH(ItemAnswerBinding.inflate(parent.layoutInflate(), parent,false))
    }

    override fun layoutResource(position: Int): Int {
        return R.layout.item_answer
    }

    override fun areItemsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean = oldItem == newItem
}