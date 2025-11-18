package com.hieunt.base.presentations.feature.container

import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityContainerBinding
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.widget.logEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContainerActivity : BaseActivity<ActivityContainerBinding>(ActivityContainerBinding::inflate) {
    @Inject
    lateinit var sharePref: SharePrefUtils

    companion object {
        var isOpenApp = false
        var isOpenHome = false
    }

    override fun initView() {
        logEvent(EventName.home_open)
        if (sharePref.countOpenApp <= 10 && !isOpenApp) {
            isOpenApp = true
            logEvent(EventName.home_open + "_" + sharePref.countOpenApp)
        }
        if (!isOpenHome) {
            isOpenApp = true
            sharePref.countOpenHome += 1
        }
    }

    override fun onResume() {
        super.onResume()
        logEvent(EventName.home_view)
    }

    override fun dataCollect() {

    }
}