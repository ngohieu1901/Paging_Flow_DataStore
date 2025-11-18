package com.hieunt.base.presentations.components.dialogs

import com.hieunt.base.base.BaseDialogFragment
import com.hieunt.base.databinding.DialogWarningPermissionBinding
import com.hieunt.base.widget.goToSetting
import com.hieunt.base.widget.tap

class WarningPermissionDialogFragment: BaseDialogFragment<DialogWarningPermissionBinding>(DialogWarningPermissionBinding::inflate) {
    override fun setupView() {
        binding.tvGoToSetting.tap {
           context?.goToSetting(requireActivity())
            dismiss()
        }
    }

    override fun initData() {
    }

    override fun dataCollect() {

    }
}