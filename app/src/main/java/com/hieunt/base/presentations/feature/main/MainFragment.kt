package com.hieunt.base.presentations.feature.main

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.hieunt.base.base.BaseFragment
import com.hieunt.base.databinding.FragmentMainBinding
import com.hieunt.base.widget.launchAndRepeatWhenViewStarted
import com.hieunt.base.widget.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val viewModel: MainViewModel by viewModels()
    private val fixturePagingAdapter by lazy {
        FixturePagingAdapter()
    }

    override fun initData() {

    }

    override fun setupView() {
        binding.rvLeague.adapter = fixturePagingAdapter.withLoadStateFooter(
            footer = FixtureLoadStateAdapter { fixturePagingAdapter.retry() }
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            fixturePagingAdapter.refresh()
        }
    }

    override fun dataCollect() {
        launchAndRepeatWhenViewStarted({
            viewModel.fixtureStream.collectLatest {
                fixturePagingAdapter.submitData(it)
            }
        }, {
            fixturePagingAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading

                (loadStates.refresh as? LoadState.Error)?.let {
                    toast(it.error.localizedMessage)
                }

                val isListEmpty = loadStates.refresh is LoadState.NotLoading && fixturePagingAdapter.itemCount == 0

                binding.rvLeague.isVisible = !isListEmpty
                binding.tvEmptyData.isVisible = isListEmpty
            }
        })
    }
}