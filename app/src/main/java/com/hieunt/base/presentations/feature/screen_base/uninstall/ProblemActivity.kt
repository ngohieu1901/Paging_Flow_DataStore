package com.hieunt.base.presentations.feature.screen_base.uninstall

import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityProblemBinding
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.feature.container.ContainerActivity
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProblemActivity : BaseActivity<ActivityProblemBinding>(ActivityProblemBinding::inflate) {
    override fun initView() {
        binding.apply {
            listOf(tvExplore, tvTryAgain, noUninstall, ivBack).forEachIndexed { int, button ->
                button.tap {
                    when (int) {
                        0 -> logEvent(EventName.uninstall_explore_click)
                        1 -> logEvent(EventName.uninstall_try_again_click)
                        2 -> logEvent(EventName.dontuninstall_click)
                    }
                    launchActivity(ContainerActivity::class.java)
                    finishAffinity()
//                    if (SystemUtils.haveNetworkConnection(this@ProblemActivity)){
//                        launchActivity(ContainerActivity::class.java)
//                        finishAffinity()
//                    } else {
//                        launchActivity(Bundle().apply {
//                            putString(SCREEN, SPLASH_ACTIVITY)
//                        }, NoInternetActivity::class.java)
//                    }
                }
            }
            stillUninstall.tap {
                logEvent(EventName.still_want_uninstall_click)
                launchActivity(UninstallActivity::class.java)
            }
        }
    }

    override fun dataCollect() {

    }

}