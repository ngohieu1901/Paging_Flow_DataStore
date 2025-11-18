package com.hieunt.base.presentations.feature.screen_base.language_start_new

import android.view.ViewGroup
import com.hieunt.base.R
import com.hieunt.base.base.BaseAdapter
import com.hieunt.base.base.BaseViewHolder
import com.hieunt.base.databinding.ItemLanguageSubBinding
import com.hieunt.base.domain.model.LanguageSubModel
import com.hieunt.base.presentations.feature.screen_base.language_start_new.LanguageSubAdapter.LanguageSubVH
import com.hieunt.base.widget.layoutInflate

class LanguageSubAdapter(
    private val onClickSubLanguage: (LanguageSubModel) -> Unit
): BaseAdapter<LanguageSubModel, LanguageSubVH>()  {
    override fun createViewHolder(
        viewType: Int,
        parent: ViewGroup,
    ): LanguageSubVH = LanguageSubVH(ItemLanguageSubBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_language_sub

    override fun areItemsTheSame(
        oldItem: LanguageSubModel,
        newItem: LanguageSubModel,
    ): Boolean = oldItem.languageName == newItem.languageName

    override fun areContentsTheSame(
        oldItem: LanguageSubModel,
        newItem: LanguageSubModel,
    ): Boolean = oldItem == newItem

    inner class LanguageSubVH(binding: ItemLanguageSubBinding): BaseViewHolder<LanguageSubModel, ItemLanguageSubBinding>(binding = binding) {
        override fun bindData(data: LanguageSubModel) {
            super.bindData(data)
            binding.ivFlag.setImageResource(data.flag)
            binding.tvLanguage.text = data.languageName
            binding.rbLanguage.isChecked = data.isCheck == true
            binding.root.setOnClickListener {
                onClickSubLanguage.invoke(data)
            }
            binding.rbLanguage.setOnClickListener {
                binding.root.performClick()
            }
            binding.rbLanguage.isChecked = data.isCheck
        }
    }
}