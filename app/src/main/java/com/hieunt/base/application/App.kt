package com.hieunt.base.application

import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.application.AdsApplication
import com.hieunt.base.R
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : AdsApplication() {

    override fun onCreate() {
        super.onCreate()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
    }
    override fun getAppTokenAdjust(): String {
        return getString(R.string.adjust_key)
    }

    override fun getFacebookID(): String {
        return getString(R.string.facebook_id)
    }
}