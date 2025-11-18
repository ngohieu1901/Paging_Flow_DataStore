package com.hieunt.base.presentations.feature.screen_base.language_start

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.BannerCallback
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.NativeCallback
import com.amazic.library.organic.TechManager
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.hieunt.base.R
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityLanguageStartBinding
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.INTER_SPLASH
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_LANG
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_LANG_2
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.firebase.event.ParamName
import com.hieunt.base.presentations.feature.screen_base.intro.IntroActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.isShowNativeLanguagePreloadAtSplash
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.nativeLanguagePreload
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.utils.SystemUtils
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import com.hieunt.base.widget.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LanguageStartActivity : BaseActivity<ActivityLanguageStartBinding>(ActivityLanguageStartBinding::inflate) {
    companion object {
        var isLogEventLanguageUserView = false
        var nativeIntroPreload: NativeAd? = null
        var isShowNativeIntroPreloadAtSplash = false
    }

    private val viewModel : LanguageStartViewModel by viewModels()
    private lateinit var adapter: LanguageStartAdapter
    private var isoLanguage : String = ""
    private var nameLanguage : String = ""
    private var isSelectedLanguage = false
    private var isPause = false
    private var countOpenSplash = 1L
    private val TAG = "LanguageStartActivity"

    @Inject
    lateinit var sharePref: SharePrefUtils

    override fun handleOnBackPressed() {
        finishAffinity()
    }

    override fun initView() {
        logEvent(EventName.language_fo_open)
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.language_fo_open + "_" + sharePref.countOpenApp)
        }

        Admob.getInstance().loadBannerAds(
            this,
            AdmobApi.getInstance().getListIDByName(RemoteName.BANNER_SETTING),
            binding.bannerSetting,
            object : BannerCallback() {},
            {},
            RemoteName.BANNER_SETTING
        )

        val nativeManager = loadNative(
            NATIVE_LANG,
            NATIVE_LANG_2,
            NATIVE_LANG,
            NATIVE_LANG_2,
            R.layout.ads_native_large_button_above,
            R.layout.ads_shimmer_large_button_above,
        )

        preloadANativeMainIntro()

        viewModel.initListLanguage()

        if (SystemUtils.getPreLanguage(this).isBlank()) {
            binding.cvSave.apply {
                setCardBackgroundColor("#D1D5DB".toColorInt())
                isEnabled = false
            }
        }

        adapter = LanguageStartAdapter(onClick = {
            logEvent(EventName.language_fo_choose)
            if (sharePref.countOpenApp <= 10 && !isSelectedLanguage) {
                logEvent(EventName.language_fo_choose + "_" + sharePref.countOpenApp)
            }
            if(!isSelectedLanguage){
                nativeManager?.reloadAdNow()
            }
            isSelectedLanguage = true
            SystemUtils.setLocale(this)
            isoLanguage = it.isoLanguage
            nameLanguage = it.languageName
            viewModel.setSelectLanguage(it.isoLanguage)
            binding.tvSelectLanguage.text = getLocalizedString(this, isoLanguage, R.string.please_select_language_to_continue)
            binding.tvLanguage.text = getLocalizedString(this, isoLanguage, R.string.Language)
            binding.tvSave.text = getLocalizedString(this, isoLanguage, R.string.save)
            binding.cvSave.apply {
                setCardBackgroundColor("#E42427".toColorInt())
                isEnabled = true
            }
        })

        binding.recyclerView.adapter = adapter

        binding.cvSave.tap {
            if (Admob.getInstance().checkCondition(this, INTER_SPLASH) &&
                !TechManager.getInstance().isTech(this) &&
                Admob.getInstance().interstitialAdSplash != null
            ) {
                Admob.getInstance().showInterAds(this, Admob.getInstance().interstitialAdSplash, object : InterCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        startNextAct()
                    }
                }, false, INTER_SPLASH)
            } else {
                startNextAct()
            }
        }
    }

    private fun startNextAct() {
        logEvent(EventName.language_fo_save_click, bundle = Bundle().apply {putString(ParamName.language_name, nameLanguage)})
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.language_fo_save_click + "_" + sharePref.countOpenApp)
        }
        sharePref.isFirstSelectLanguage = false
        SystemUtils.setPreLanguage(this@LanguageStartActivity, isoLanguage)
        SystemUtils.setLocale(this)
        launchActivity(IntroActivity::class.java)
        finish()
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collect {
                    when (it) {
                        is LanguageUiState.Idle -> {}

                        is LanguageUiState.Language -> {
                            dismissLoading()
                            adapter.submitList(it.listLanguage)
                        }
                        is LanguageUiState.Loading -> {
                            showLoading()
                        }

                        is LanguageUiState.Error -> {
                            toast(it.e.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun getLocalizedString(context: Context, languageCode: String, resId: Int): String {
        val localeParts = languageCode.split("-")
        val myLocale = if (localeParts.size > 1) {
            Locale(localeParts[0], localeParts[1])
        } else {
            Locale(languageCode)
        }
        val config = Configuration(context.resources.configuration)
        config.setLocale(myLocale)
        val localizedContext = context.createConfigurationContext(config)
        return localizedContext.resources.getString(resId)
    }

    private fun preloadANativeMainIntro() {
        Admob.getInstance().loadNativeAds(
            this,
            AdmobApi.getInstance().getListIDByName(RemoteName.NATIVE_INTRO),
            object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd?) {
                    super.onNativeAdLoaded(nativeAd)
                    nativeIntroPreload = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    isShowNativeIntroPreloadAtSplash = true
                }
            }, RemoteName.NATIVE_INTRO
        )
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeLanguagePreloadAtSplash()
            }
            Log.d(TAG, "isShowSplashAds: ${SplashActivity.isShowSplashAds} - isCloseSplashAds: ${SplashActivity.isCloseSplashAds}")
            if (SplashActivity.isShowSplashAds) {
                if (SplashActivity.isCloseSplashAds) {
                    if (!isLogEventLanguageUserView && !isPause) {
                        EventTrackingHelper.logEvent(this, "language_user_view")
                        if (countOpenSplash <= 10) {
                            Log.d(TAG, "logEventOnResume: $countOpenSplash")
                            EventTrackingHelper.logEvent(this, "language_user_view" + "_${countOpenSplash}")
                            isLogEventLanguageUserView = true
                        }
                    }
                }
            } else {
                if (isLogEventLanguageUserView && !isPause) {
                    EventTrackingHelper.logEvent(this, "language_user_view")
                    if (countOpenSplash <= 10) {
                        Log.d(TAG, "logEventOnResume: $countOpenSplash")
                        EventTrackingHelper.logEvent(this, "language_user_view" + "_${countOpenSplash}")
                        isLogEventLanguageUserView = true
                    }
                }
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
        Log.d(TAG, "onPause: ")
    }

    private fun showNativeLanguagePreloadAtSplash() {
        if (nativeLanguagePreload != null && !isShowNativeLanguagePreloadAtSplash && !TechManager.getInstance().isTech(this)) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.ads_native_large_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguagePreload, adView)
        }
    }
}