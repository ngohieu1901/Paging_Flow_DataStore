package com.hieunt.base.presentations.feature.main

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hieunt.base.base.BaseFragment
import com.hieunt.base.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val viewModel: MainViewModel by viewModels()

    override fun initData() {

    }

    override fun setupView() {
        viewModel.getAllFixtureByDate()
    }

    override fun dataCollect() {

    }
}