package com.hieunt.base.presentations.feature.screen_base.uninstall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hieunt.base.R
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityUninstallBinding
import com.hieunt.base.di.IoDispatcher
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.feature.container.ContainerActivity
import com.hieunt.base.widget.gone
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import com.hieunt.base.widget.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri

@AndroidEntryPoint
class UninstallActivity : BaseActivity<ActivityUninstallBinding>(ActivityUninstallBinding::inflate) {
    private val viewmodel: UninstallViewModel by viewModels()
    private lateinit var uninstallAdapter: UninstallAdapter

    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    override fun initView() {
        binding.apply {
            uninstallAdapter = UninstallAdapter { data, pos ->
                viewmodel.updateListAnswer(data)
                if (pos == viewmodel.currentState.listAnswer.size - 1) {
                    edAnswer.visible()
                } else {
                    edAnswer.gone()
                }
            }

            rvAnswer.adapter = uninstallAdapter
            rvAnswer.itemAnimator = null

            tvUninstall.tap {
                lifecycleScope.launch(exceptionHandler + ioDispatcher) {
                    val answerResource = viewmodel.currentState.listAnswer.first{ answers -> answers.isSelected}.name
                    if (answerResource != R.string.others) {
                        logEvent(EventName.reason_uninstall, Bundle().apply { putString("reason", getString(answerResource))})
                    } else {
                        logEvent(EventName.reason_uninstall, Bundle().apply { putString("reason", edAnswer.text.toString())})
                    }
                }

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
            tvCancel.tap {
                logEvent(EventName.uninstall_canel_click)
                launchActivity(ContainerActivity::class.java)
                finishAffinity()
//                if (SystemUtils.haveNetworkConnection(this@UninstallActivity)){
//                    launchActivity(ContainerActivity::class.java)
//                    finishAffinity()
//                } else {
//                    launchActivity(Bundle().apply {
//                        putString(SCREEN, SPLASH_ACTIVITY)
//                    }, NoInternetActivity::class.java)
//                }
            }
            ivBack.tap {
                finish()
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewmodel.uiStateStore.collect {
                uninstallAdapter.submitList(it.listAnswer)
            }
        }
    }

}