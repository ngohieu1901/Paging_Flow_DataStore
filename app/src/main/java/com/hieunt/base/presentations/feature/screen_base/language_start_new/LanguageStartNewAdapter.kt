package com.hieunt.base.presentations.feature.screen_base.language_start_new

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import com.hieunt.base.domain.model.LanguageParentModel
import com.hieunt.base.R
import com.hieunt.base.base.BaseAdapter
import com.hieunt.base.base.BaseViewHolder
import com.hieunt.base.databinding.ItemLanguageNewBinding
import com.hieunt.base.presentations.feature.screen_base.language_start_new.LanguageStartNewAdapter.LanguageStartNewVH
import com.hieunt.base.widget.gone
import com.hieunt.base.widget.layoutInflate
import com.hieunt.base.widget.tap
import com.hieunt.base.widget.visible

class LanguageStartNewAdapter(
    private val onSelectLanguage: (String, String) -> Unit,
    private val onExpand: (LanguageParentModel) -> Unit,
): BaseAdapter<LanguageParentModel, LanguageStartNewVH>() {

    override fun createViewHolder(
        viewType: Int,
        parent: ViewGroup,
    ): LanguageStartNewVH = LanguageStartNewVH(ItemLanguageNewBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_language_new

    override fun areItemsTheSame(
        oldItem: LanguageParentModel,
        newItem: LanguageParentModel,
    ): Boolean = oldItem.languageName == newItem.languageName

    override fun areContentsTheSame(
        oldItem: LanguageParentModel,
        newItem: LanguageParentModel,
    ): Boolean = oldItem == newItem

    inner class LanguageStartNewVH(binding: ItemLanguageNewBinding): BaseViewHolder<LanguageParentModel, ItemLanguageNewBinding>(binding = binding) {
        override fun bindData(data: LanguageParentModel) {
            super.bindData(data)
            binding.apply {
                if (data.isExpand) {
                    binding.tvTitle.setTextColor("#FFFFFF".toColorInt())
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.imgDropdown.setImageResource(R.drawable.ic_arrow_down)
                    binding.rlLayoutParent.setBackgroundResource(R.drawable.bg_item_language_select)
                } else {
                    binding.tvTitle.setTextColor("#000000".toColorInt())
                    binding.recyclerView.visibility = View.GONE
                    binding.imgDropdown.setImageResource(R.drawable.ic_arrow_right)
                    binding.rlLayoutParent.setBackgroundResource(R.drawable.bg_item_language)
                }

                binding.recyclerView.adapter = LanguageSubAdapter {
                    onSelectLanguage(it.languageName, it.isoLanguage)
                }.apply { submitList(data.listLanguageSubModel) }

                binding.rbLanguage.isChecked = data.isCheck
                binding.rbLanguage.tap {
                    onSelectLanguage(data.languageName, data.isoLanguage)
                }

                binding.imgAvatar.setImageResource(data.image)
                binding.tvTitle.text = data.languageName

                if (data.listLanguageSubModel.isEmpty()) {
                    binding.imgDropdown.gone()
                    binding.rbLanguage.visible()
                } else {
                    binding.imgDropdown.visible()
                    binding.rbLanguage.gone()
                }
            }
        }

        override fun onItemClickListener(data: LanguageParentModel) {
            super.onItemClickListener(data)
            if (data.listLanguageSubModel.isEmpty()) {
                onSelectLanguage(data.languageName, data.isoLanguage)
            } else {
                onExpand(data)
            }
        }
    }
}