package com.hieunt.base.presentations.feature.screen_base.intro

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.amazic.library.Utils.EventTrackingHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.organic.TechManager
import com.google.android.gms.ads.nativead.NativeAdView
import com.hieunt.base.R
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.databinding.ActivityIntroBinding
import com.hieunt.base.domain.model.IntroModel
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.INTER_INTRO
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO_2
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.components.dialogs.RatingDialogFragment
import com.hieunt.base.presentations.feature.container.ContainerActivity
import com.hieunt.base.presentations.feature.screen_base.language_start.LanguageStartActivity.Companion.isShowNativeIntroPreloadAtSplash
import com.hieunt.base.presentations.feature.screen_base.language_start.LanguageStartActivity.Companion.nativeIntroPreload
import com.hieunt.base.presentations.feature.screen_base.permission.PermissionActivity
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.widget.gone
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>(ActivityIntroBinding::inflate) {
    private var listIntroModel = mutableListOf<IntroModel>()
    private lateinit var introAdapter: IntroAdapter

    @Inject
    lateinit var sharePref: SharePrefUtils

    var isFirst = true
    private var isPause = false

    private val myPageChangeCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (isFirst) {
                    isFirst = false
                    return
                }

                when (listIntroModel[position].type) {
                    IntroType.GUIDE_1 -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_1_view")
                    }

                    IntroType.ADS -> {
                        introAdapter.notifyNativeAdFullScreen()
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_2_view")
                    }

                    IntroType.GUIDE_2 -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_3_view")
                    }

                    IntroType.GUIDE_3 -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_4_view")
                    }

                    IntroType.ADS_1 -> {
                        introAdapter.notifyNativeAdFullScreen1()
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_5_view")
                    }

                    IntroType.GUIDE_4 -> {
                        EventTrackingHelper.logEvent(this@IntroActivity, "Onboarding_6_view")
                    }
                }
                binding.apply {
                    if (listIntroModel[position].type == IntroType.ADS || listIntroModel[position].type == IntroType.ADS_1 ) {
                        listOf(frAds, linearDots, btnNextTutorial).forEach {
                            it.gone()
                        }
                    } else {
                        listOf(frAds, linearDots, btnNextTutorial).forEach {
                            it.visible()
                        }
                    }
                }
                addBottomDots(position)
            }
        }

    override fun initView() {
        logEvent(EventName.onboarding_1_view)
        logEvent(EventName.onboard_open)

        if (sharePref.countOpenApp <= 10) {
            logEvent(EventName.onboard_open + "_" + sharePref.countOpenApp)
        }

        loadNative(
            NATIVE_INTRO,
            NATIVE_INTRO_2,
            NATIVE_INTRO,
            NATIVE_INTRO_2,
            R.layout.ads_native_small_button_above,
            R.layout.ads_shimmer_small_button_above,
        )

        introAdapter = IntroAdapter(this, initData()) {
            binding.viewPager2.currentItem += 1
        }

        binding.viewPager2.apply {
            adapter = introAdapter
            registerOnPageChangeCallback(myPageChangeCallback)
        }

        addBottomDots(0)

        binding.btnNextTutorial.setOnClickListener {
            if (binding.viewPager2.currentItem == listIntroModel.size - 1) {
                if (listOf(2, 5, 9).contains(sharePref.countOpenHome) && !sharePref.isRated && sharePref.isPassPermission) {
                    RatingDialogFragment(
                        isFinishActivity = false,
                        onClickRate = {},
                        onDismissListener = {
                            loadAndShowInter(INTER_INTRO, INTER_INTRO) {
                                logEvent(EventName.onboarding_next_click)
                                if (sharePref.countOpenApp <= 10) {
                                    logEvent(EventName.onboarding_next_click + "_" + sharePref.countOpenApp)
                                }
                                startNextScreen()
                            }
                        },
                    ).show(supportFragmentManager, "RatingDialogFragment")
                } else {
                    loadAndShowInter(INTER_INTRO, INTER_INTRO) {
                        logEvent(EventName.onboarding_next_click)
                        if (sharePref.countOpenApp <= 10) {
                            logEvent(EventName.onboarding_next_click + "_" + sharePref.countOpenApp)
                        }
                        startNextScreen()
                    }
                }
            } else {
                binding.viewPager2.currentItem += 1
            }
        }
    }

    override fun dataCollect() {

    }

    private fun initData(): MutableList<IntroModel> {
        addBottomDots(0)
        listIntroModel = mutableListOf<IntroModel>().apply {
            add(
                IntroModel(
                    R.drawable.img_intro_1,
                    R.string.title_intro_1,
                    R.string.content_intro_1,
                    IntroType.GUIDE_1
                )
            )
            if (Admob.getInstance().checkCondition(
                    this@IntroActivity,
                    RemoteName.NATIVE_INTRO_FULL
                ) || Admob.getInstance()
                    .checkCondition(this@IntroActivity, RemoteName.NATIVE_INTRO_FULL_2)
            ) {
                add(
                    IntroModel(
                        R.drawable.ic_logo_app,
                        R.string.app_name,
                        R.string.app_name,
                        IntroType.ADS
                    )
                )
            }
            add(
                IntroModel(
                    R.drawable.img_intro_2,
                    R.string.title_intro_2,
                    R.string.content_intro_2,
                    IntroType.GUIDE_2
                )
            )
            add(
                IntroModel(
                    R.drawable.img_intro_3,
                    R.string.title_intro_3,
                    R.string.content_intro_3,
                    IntroType.GUIDE_3
                )
            )
            if (Admob.getInstance().checkCondition(
                    this@IntroActivity,
                    RemoteName.NATIVE_INTRO_FULL1
                ) || Admob.getInstance()
                    .checkCondition(this@IntroActivity, RemoteName.NATIVE_INTRO_FULL1_2)
            ) {
                add(
                    IntroModel(
                        R.drawable.ic_logo_app,
                        R.string.app_name,
                        R.string.app_name,
                        IntroType.ADS_1
                    )
                )
            }
            add(
                IntroModel(
                    R.drawable.img_intro_4,
                    R.string.title_intro_4,
                    R.string.content_intro_4,
                    IntroType.GUIDE_4
                )
            )

        }
        return listIntroModel
    }

    private fun startNextScreen() {
        launchActivity(if (sharePref.isPassPermission) ContainerActivity::class.java else PermissionActivity::class.java)
        finishAffinity()
    }

    private fun addBottomDots(currentPage: Int) {
        binding.linearDots.removeAllViews()
        val dots = arrayOfNulls<ImageView>(listIntroModel.size)
        for (i in 0 until listIntroModel.size) {
            dots[i] = ImageView(this)
            if (i == currentPage)
                dots[i]!!.setImageResource(R.drawable.ic_intro_selected)
            else
                dots[i]!!.setImageResource(R.drawable.ic_intro_not_select)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            binding.linearDots.addView(dots[i], params)
        }
    }

    override fun handleOnBackPressed() {
        finishAffinity()
    }

    override fun onResume() {
        super.onResume()
        isPause = false
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isPause) {
                showNativeIntroPreloadAtSplash()
            }
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    private fun showNativeIntroPreloadAtSplash() {
        if (nativeIntroPreload != null && !isShowNativeIntroPreloadAtSplash && !TechManager.getInstance().isTech(this)) {
            val adView: NativeAdView = layoutInflater.inflate(R.layout.ads_native_small_button_above, binding.frAds, false) as NativeAdView
            binding.frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeIntroPreload, adView)
        }
    }
}