package com.hieunt.base.presentations.components.bottom_sheet

import com.hieunt.base.base.BaseBottomSheetDialog
import com.hieunt.base.databinding.BottomSheetExitAppBinding
import com.hieunt.base.widget.tap

class ExitAppBottomSheet(
    private val onExit: () -> Unit
): BaseBottomSheetDialog<BottomSheetExitAppBinding>(BottomSheetExitAppBinding::inflate) {
    override fun setupView() {
        binding.tvCancel.tap {
            dismiss()
        }
        binding.tvExit.tap {
            onExit()
        }
    }

    override fun initData() {

    }

    override fun dataCollect() {

    }
}