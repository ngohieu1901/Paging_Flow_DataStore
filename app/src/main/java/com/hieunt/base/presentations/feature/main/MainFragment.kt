package com.hieunt.base.presentations.feature.main

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
    }

    override fun dataCollect() {
        launchAndRepeatWhenViewStarted({
            viewModel.fixtureStream.collectLatest {
                fixturePagingAdapter.submitData(it)
            }
        }, {
            fixturePagingAdapter.loadStateFlow.collectLatest { loadStates ->
                val newState = when (val refreshState = loadStates.refresh) {
                    is LoadState.Loading -> MainUiState.Loading
                    is LoadState.Error -> MainUiState.Error(refreshState.error.localizedMessage)
                    is LoadState.NotLoading -> {
                        MainUiState.Success(isEmpty = fixturePagingAdapter.itemCount == 0)
                    }
                }
                handleUiState(newState)
            }
        })
    }

    private fun handleUiState(state: MainUiState) {
        when (state) {
            is MainUiState.Loading -> showLoading()

            is MainUiState.Success -> dismissLoading()

            is MainUiState.Error -> {
                dismissLoading()
                toast(state.message)
            }
            else -> {}
        }
    }
}