package com.hieunt.base.presentations.feature.screen_base.no_internet

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityNoInternetBinding
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.widget.tap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoInternetActivity: BaseActivity<ActivityNoInternetBinding>(ActivityNoInternetBinding::inflate) {
    override fun initView() {
        binding.tvTryAgain.tap {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
            } else {
                AdsHelper.disableResume(this)
                val wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(wifiSettingsIntent)
            }
        }
    }

    override fun dataCollect() {

    }

    override fun handleOnBackPressed() {
        finishAffinity()
    }
}