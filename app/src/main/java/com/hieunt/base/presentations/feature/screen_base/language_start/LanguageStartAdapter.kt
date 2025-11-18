package com.hieunt.base.presentations.feature.screen_base.language_start

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.hieunt.base.R
import com.hieunt.base.base.BaseSyncDifferAdapter
import com.hieunt.base.base.BaseViewHolder
import com.hieunt.base.databinding.ItemLanguageBinding
import com.hieunt.base.domain.model.LanguageModelNew
import com.hieunt.base.widget.gone
import com.hieunt.base.widget.layoutInflate
import com.hieunt.base.widget.visible

class LanguageStartAdapter(
    val onClick: (lang: LanguageModelNew) -> Unit
) : BaseSyncDifferAdapter<LanguageModelNew, LanguageStartAdapter.LanguageVH>() {

    inner class LanguageVH(binding: ItemLanguageBinding) :
        BaseViewHolder<LanguageModelNew, ItemLanguageBinding>(binding) {
        override fun bindData(data: LanguageModelNew) {
            super.bindData(data)
            binding.apply {
                data.image?.let { binding.civLogo.setImageResource(it) }
                binding.tvName.text = data.languageName

                binding.layoutItem.setOnClickListener { onClick(data) }

                if (data.isCheck) {
                    binding.layoutItem.setBackgroundResource(R.drawable.bg_item_language_select)
                    binding.tvName.typeface = ResourcesCompat.getFont(binding.root.context, R.font.poppins_semibold)
                    binding.tvName.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    binding.layoutItem.setBackgroundResource(R.drawable.bg_item_language)
                    binding.tvName.typeface = ResourcesCompat.getFont(binding.root.context, R.font.poppins_semibold)
                    binding.tvName.setTextColor(Color.parseColor("#374151"))
                }

                if (data.isShowAnim) {
                    binding.animationView.visible()
                    binding.animationView.playAnimation()
                } else {
                    binding.animationView.gone()
                    binding.animationView.pauseAnimation()
                }
            }
        }

        override fun onItemClickListener(data: LanguageModelNew) {
            super.onItemClickListener(data)
            onClick.invoke(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): LanguageVH = LanguageVH(
        ItemLanguageBinding.inflate(parent.layoutInflate(), parent, false)
    )

    override fun layoutResource(position: Int): Int = R.layout.item_language
    override fun areItemsTheSame(oldItem: LanguageModelNew, newItem: LanguageModelNew): Boolean = oldItem.isoLanguage == newItem.isoLanguage

    override fun areContentsTheSame(oldItem: LanguageModelNew, newItem: LanguageModelNew): Boolean = oldItem == newItem
}