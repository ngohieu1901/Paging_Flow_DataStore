package com.hieunt.base.presentations.feature.screen_base.language_start_new

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
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
import com.hieunt.base.databinding.ActivityLanguageStartNewBinding
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_LANG
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_LANG_2
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.firebase.event.ParamName
import com.hieunt.base.presentations.feature.screen_base.intro.IntroActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.isShowNativeClickLanguagePreloadAtSplash
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.isShowNativeLanguagePreloadAtSplash
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.nativeLanguageClickPreload
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.nativeLanguagePreload
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.utils.SystemUtils
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.launchAndRepeatWhenStarted
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import com.hieunt.base.widget.visible
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LanguageStartNewActivity : BaseActivity<ActivityLanguageStartNewBinding>(
    ActivityLanguageStartNewBinding::inflate
) {
    private val viewModel: LanguageStartNewViewModel by viewModels()

    @Inject
    lateinit var sharePref: SharePrefUtils

    private lateinit var adapter: LanguageStartNewAdapter

    private var isPause = false
    private var isChooseLanguage = false
    private var languageName = ""
    private var languageCode = ""

    companion object {
        var isLogEventLanguageUserView = false
        var nativeIntroPreload: NativeAd? = null
        var isShowNativeIntroPreloadAtSplash = false
    }

    override fun initView() {
        viewModel.initLanguagesStart()

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
            remoteKey = NATIVE_LANG,
            remoteKeySecondary = NATIVE_LANG_2,
            adsKeyMain = NATIVE_LANG,
            adsKeySecondary = NATIVE_LANG_2,
            idLayoutNative = R.layout.ads_native_large_button_above,
            idLayoutShimmer = R.layout.ads_shimmer_large_button_above,
            isAlwaysReloadOnResume = false
        )

        preloadANativeMainIntro()

        adapter = LanguageStartNewAdapter(
            onSelectLanguage = { languageName, languageCode ->
                this@LanguageStartNewActivity.languageName = languageName
                this@LanguageStartNewActivity.languageCode = languageCode

                viewModel.selectLanguage(languageName)

                logEvent(EventName.language_fo_choose)
                if (sharePref.countOpenApp <= 10 && !isChooseLanguage) {
                    logEvent(EventName.language_fo_choose + "_" + sharePref.countOpenApp)
                }
                if (!isChooseLanguage) {
                    isChooseLanguage = true
                    nativeManager?.cancelAutoReloadNative()
                    showNativeClickLanguagePreloadAtSplash()
                }
                SystemUtils.setLocale(this)
                binding.ivDone.visible()

                binding.tvSelectLanguage.text = getLocalizedString(
                    this,
                    languageCode,
                    R.string.please_select_language_to_continue
                )
                binding.tvLanguage.text = getLocalizedString(this, languageCode, R.string.Language)
                binding.tvTitleApply.text =
                    getLocalizedString(this, languageCode, R.string.applying_your_language_settings)
                binding.tvContentApply.text = getLocalizedString(
                    this,
                    languageCode,
                    R.string.we_re_setting_up_your_language_preferences
                )
            },
            onExpand = {
                nativeManager?.cancelAutoReloadNative()
                showNativeClickLanguagePreloadAtSplash()
                viewModel.handleExpand(it)
            }
        )
        binding.recyclerView.adapter = adapter

        binding.ivDone.tap {
            binding.llApplyLanguage.visibility = View.VISIBLE
            if (Admob.getInstance().checkCondition(this, "inter_splash") &&
                !TechManager.getInstance().isTech(this) &&
                Admob.getInstance().interstitialAdSplash != null
            ) {
                Admob.getInstance().showInterAds(
                    this,
                    Admob.getInstance().interstitialAdSplash,
                    object : InterCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            startNextAct()
                        }
                    },
                    false,
                    "inter_splash"
                )
            } else {
                startNextAct()
            }
        }
    }

    private fun startNextAct() {
        logEvent(
            EventName.language_fo_save_click,
            bundle = Bundle().apply { putString(ParamName.language_name, languageName) })
        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.language_fo_save_click + "_" + sharePref.countOpenApp)
        }
        sharePref.isFirstSelectLanguage = false
        SystemUtils.setPreLanguageName(this@LanguageStartNewActivity, languageName)
        SystemUtils.setPreLanguage(this@LanguageStartNewActivity, languageCode)
        SystemUtils.setLocale(this)
        launchActivity(IntroActivity::class.java)
        finish()
    }

    override fun dataCollect() {
        launchAndRepeatWhenStarted({
            viewModel.uiStateStore.collectLatest {
                adapter.submitList(it.listLanguage)
            }
        })
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

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeLanguagePreloadAtSplash()
            }
            if (SplashActivity.isShowSplashAds) {
                if (SplashActivity.isCloseSplashAds) {
                    if (!isLogEventLanguageUserView && !isPause) {
                        EventTrackingHelper.logEvent(this, "language_user_view")
                        if (sharePref.countOpenApp <= 10) {
                            EventTrackingHelper.logEvent(
                                this,
                                "language_user_view" + "_${sharePref.countOpenApp}"
                            )
                            isLogEventLanguageUserView = true
                        }
                    }
                }
            } else {
                if (isLogEventLanguageUserView && !isPause) {
                    EventTrackingHelper.logEvent(this, "language_user_view")
                    if (sharePref.countOpenApp <= 10) {
                        EventTrackingHelper.logEvent(
                            this,
                            "language_user_view" + "_${sharePref.countOpenApp}"
                        )
                        isLogEventLanguageUserView = true
                    }
                }
            }
        }, 1000)
    }

    private fun showNativeLanguagePreloadAtSplash() {
        if (nativeLanguagePreload != null && !isShowNativeLanguagePreloadAtSplash && !TechManager.getInstance()
                .isTech(this)
        ) {
            val adView: NativeAdView = layoutInflater.inflate(
                R.layout.ads_native_large_button_above,
                binding.frAds,
                false
            ) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguagePreload, adView)
        }
    }

    private fun showNativeClickLanguagePreloadAtSplash() {
        if (nativeLanguageClickPreload != null && !isShowNativeClickLanguagePreloadAtSplash) {
            val adView: NativeAdView = layoutInflater.inflate(
                R.layout.ads_native_large_button_above,
                binding.frAds,
                false
            ) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeLanguageClickPreload, adView)
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
}