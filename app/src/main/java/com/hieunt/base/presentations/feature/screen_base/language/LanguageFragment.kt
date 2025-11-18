package com.hieunt.base.presentations.feature.screen_base.language

import androidx.fragment.app.viewModels
import com.hieunt.base.base.BaseFragment
import com.hieunt.base.databinding.FragmentLanguageBinding
import com.hieunt.base.presentations.feature.container.ContainerActivity
import com.hieunt.base.presentations.feature.screen_base.language_start_new.LanguageStartNewAdapter
import com.hieunt.base.presentations.feature.screen_base.language_start_new.LanguageStartNewViewModel
import com.hieunt.base.utils.SystemUtils
import com.hieunt.base.widget.finishAffinity
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.launchAndRepeatWhenStarted
import com.hieunt.base.widget.tap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    private lateinit var adapter: LanguageStartNewAdapter
    private val viewModel : LanguageStartNewViewModel by viewModels()

    override fun initData() {
        viewModel.initLanguagesSetting()
    }

    override fun setupView() {
        adapter = LanguageStartNewAdapter(
            onSelectLanguage = { languageName, languageCode ->
                SystemUtils.saveLocale(requireContext(), languageCode)
                SystemUtils.setPreLanguageName(requireContext(), languageName)
                launchActivity(ContainerActivity::class.java)
                finishAffinity()
            },
            onExpand = {
                viewModel.handleExpand(it)
            }
        )

        binding.recyclerView.adapter = adapter
        binding.ivBack.tap {
            popBackStack()
        }
    }

    override fun dataCollect() {
        launchAndRepeatWhenStarted( {
            viewModel.uiStateStore.collectLatest {
                adapter.submitList(it.listLanguage)
            }
        } )
    }
}