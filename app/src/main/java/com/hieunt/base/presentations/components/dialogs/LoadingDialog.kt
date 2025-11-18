package com.hieunt.base.presentations.components.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.hieunt.base.base.BaseDialog
import com.hieunt.base.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : BaseDialog<DialogLoadingBinding>(context, false) {
    override fun initView() {

    }

    override fun initClickListener() {

    }

    override fun setViewBinding(
        inflater: LayoutInflater,
    ): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(inflater)
    }
}