package com.hieunt.base.presentations.feature.screen_base.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.amazic.library.Utils.EventTrackingHelper.native_language
import com.amazic.library.Utils.NetworkUtil
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.app_open_ads.AppOpenManager
import com.amazic.library.ads.callback.AppOpenCallback
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.ads.splash_ads.AsyncSplash
import com.amazic.library.update_app.UpdateApplicationManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.hieunt.base.R
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.constants.Constants
import com.hieunt.base.databinding.ActivitySplashBinding
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.TURN_OFF_CONFIGS
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.feature.screen_base.language_start_new.LanguageStartNewActivity
import com.hieunt.base.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    companion object {
        var isShowSplashAds = false
        var isCloseSplashAds = false
        var isShowNativeLanguagePreloadAtSplash = false
        var isShowNativeClickLanguagePreloadAtSplash = false
        var nativeLanguagePreload: NativeAd? = null
        var nativeLanguageClickPreload: NativeAd? = null
        var appUpdateManager: AppUpdateManager? = null
        var installStateUpdatedListener: InstallStateUpdatedListener? = null
    }

    @Inject
    lateinit var sharePref: SharePrefUtils

    private var isHandleAsyncSplash = false

    private val openCallback =
        object : AppOpenCallback() {
            override fun onNextAction() {
                super.onNextAction()
                startNextScreen()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                isShowSplashAds = true
            }

            override fun onAdImpression() {
                super.onAdImpression()
                AppOpenManager.getInstance().appOpenAdSplash = null
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                isCloseSplashAds = true
            }

            override fun onAdFailedToShowFullScreenContent() {
                super.onAdFailedToShowFullScreenContent()
                isCloseSplashAds = true
            }
        }

    private val interCallback =
        object : InterCallback() {
            override fun onNextAction() {
                super.onNextAction()
                startNextScreen()
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                isShowSplashAds = true
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Admob.getInstance().interstitialAdSplash = null
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                isCloseSplashAds = true
            }

            override fun onAdFailedToShowFullScreenContent() {
                super.onAdFailedToShowFullScreenContent()
                isCloseSplashAds = true
            }
        }

    private fun startNextScreen() {
        launchActivity(LanguageStartNewActivity::class.java)
//        launchActivity(ContainerActivity::class.java)
        finishAffinity()
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return
        }

        sharePref.countOpenApp += 1
        logEvent(EventName.splash_open)
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.splash_open + "_" + sharePref.countOpenApp)
        }

        lifecycleScope.launch {
            for (i in 1..100) {
                binding.progressBar.progress = i
                binding.tvProgress.text = getString(R.string.loading) + " ($i)%"
                delay(30)
            }
        }

        UpdateApplicationManager.getInstance().init(this,
            object : UpdateApplicationManager.IonUpdateApplication {
                override fun onUpdateApplicationFail() {
                    handleAsyncSplashJustOnce()
                    toast(getString(R.string.update_application_fail))
                }

                override fun onUpdateApplicationSuccess() {
                    toast(getString(R.string.update_application_success))
                }

                override fun onMustNotUpdateApplication() {
                    handleAsyncSplashJustOnce()
                }

                override fun requestUpdateFail() {
                    handleAsyncSplashJustOnce()
                }
            })

        if (NetworkUtil.isNetworkActive(this)) {
            UpdateApplicationManager.getInstance().setUseFlexibleUpdate()
            appUpdateManager =
                UpdateApplicationManager.getInstance().checkVersionPlayStore(
                    this,
                    true,
                    false,
                    getString(R.string.new_update_available),
                    getString(R.string.upgrade_now_for_a_smoother_experience_bug_fixes_for_better_performance),
                    getString(R.string.update_now),
                    getString(R.string.no),
                )
            installStateUpdatedListener =
                InstallStateUpdatedListener { state ->
                    if (state.installStatus() == InstallStatus.DOWNLOADING ||
                        state.installStatus() == InstallStatus.FAILED ||
                        state.installStatus() == InstallStatus.CANCELED ||
                        state.installStatus() == InstallStatus.UNKNOWN
                    ) {
                        Log.d("initView", ": appUpdateManage")
                        handleAsyncSplashJustOnce()
                    } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
                        Log.d("initView", ": appUpdateManage_${appUpdateManager}")
                        Toast.makeText(
                            applicationContext,
                            applicationContext.getString(R.string.updated_and_ready_welcome_back),
                            Toast.LENGTH_SHORT
                        ).show()
                        appUpdateManager?.completeUpdate()
                    } else {
                        Log.d("initView", ": appUpdateManage else")
                    }
                }
            Log.d(
                "initView",
                "appUpdateManager register. ${appUpdateManager}_${installStateUpdatedListener}"
            )
            installStateUpdatedListener?.let { appUpdateManager?.registerListener(it) }
        }
    }

    private fun handleAsyncSplashJustOnce() {
        if (!isHandleAsyncSplash) { // important
            AsyncSplash.getInstance().init(
                activity = this,
                appOpenCallback = openCallback,
                interCallback = interCallback,
                adjustKey = getString(R.string.adjust_key),
                linkServer = getString(R.string.link_server),
                appId = getString(R.string.app_id),
                jsonIdAdsDefault = "",
            )
            // Test TechManager
            AsyncSplash.getInstance().setDebug(true) // Production set to false
            AsyncSplash.getInstance().setAsyncSplashAds()
            // AsyncSplash.getInstance().setLoadAndShowIdInterAdSplashAsync() //Nếu request load đồng thời inter_splash va inter_splash_high
            AsyncSplash.getInstance().setListTurnOffRemoteKeys(TURN_OFF_CONFIGS.toMutableList())
            // Case without welcome back
//            AsyncSplash.getInstance().setInitResumeAdsNormal()
            // Case welcome back above ads resume
//            AsyncSplash.getInstance().setInitWelcomeBackAboveResumeAds(WelcomeBackActivity::class.java)
            // Case welcome back below ads resume
//            AsyncSplash.getInstance().setInitWelcomeBackBelowResumeAds(WelcomeBackActivity::class.java)
            AsyncSplash.getInstance().handleAsync(
                this,
                this,
                lifecycleScope,
                onAsyncSplashDone = {
                    preloadANativeMainLanguage()
                    preloadANativeClickLanguage()
                    AdsHelper.turnOffAllAds()
                },
                onNoInternetAction = {
                    launchActivity(
                        Bundle().apply {
                            putString(
                                Constants.IntentKeys.SCREEN,
                                Constants.IntentKeys.SPLASH_ACTIVITY,
                            )
                        },
                        NoInternetActivity::class.java,
                    )
                },
            )
            AsyncSplash.getInstance().setOnPrepareLoadInterOpenSplashAds {
                AdsHelper.turnOffAllAds()
            }
            isHandleAsyncSplash = true
        }
    }

    private fun preloadANativeMainLanguage() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(native_language),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeLanguagePreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeLanguagePreloadAtSplash = true
                }
            },
            native_language,
        )
    }

    private fun preloadANativeClickLanguage() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(RemoteName.NATIVE_CLICK),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeLanguageClickPreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeClickLanguagePreloadAtSplash = true
                }
            }, RemoteName.NATIVE_CLICK
        )
    }

    override fun dataCollect() {

    }

    override fun onResume() {
        super.onResume()
        AdsHelper.disableResume(this)
        AsyncSplash.getInstance().checkShowSplashWhenFail()
    }

    override fun handleOnBackPressed() {

    }
}