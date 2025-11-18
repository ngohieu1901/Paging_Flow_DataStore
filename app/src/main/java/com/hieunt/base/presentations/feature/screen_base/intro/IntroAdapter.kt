package com.hieunt.base.presentations.feature.screen_base.intro

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.NativeCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.hieunt.base.R
import com.hieunt.base.databinding.ItemIntroAdsNativeBinding
import com.hieunt.base.databinding.ItemIntroBinding
import com.hieunt.base.domain.model.IntroModel
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO_FULL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO_FULL1
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO_FULL1_2
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_INTRO_FULL_2
import com.hieunt.base.widget.tap
import java.util.Locale

class IntroAdapter(
    private val context: AppCompatActivity,
    private val list: List<IntroModel> = emptyList(),
    private val onCloseAds: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var nativeAdFullScreen: NativeAd? = null
    private var nativeAdFullScreen2: NativeAd? = null
    private var nativeAdFullScreen1: NativeAd? = null
    private var nativeAdFullScreen12: NativeAd? = null

    init {
        loadNativeIntroFull(NATIVE_INTRO_FULL, NATIVE_INTRO_FULL)
        loadNativeIntroFull(NATIVE_INTRO_FULL_2, NATIVE_INTRO_FULL_2)
        loadNativeIntroFull1(NATIVE_INTRO_FULL1, NATIVE_INTRO_FULL1)
        loadNativeIntroFull1(NATIVE_INTRO_FULL1_2, NATIVE_INTRO_FULL1_2)
    }

    inner class IntroDefaultVH(val binding: ItemIntroBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class IntroAdsNativeVH(val binding: ItemIntroAdsNativeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IntroType.GUIDE_1.ordinal, IntroType.GUIDE_2.ordinal, IntroType.GUIDE_3.ordinal, IntroType.GUIDE_4.ordinal -> {
                val defaultBinding =
                    ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                IntroDefaultVH(defaultBinding)
            }

            IntroType.ADS.ordinal, IntroType.ADS_1.ordinal -> {
                val adsNativeBinding = ItemIntroAdsNativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                IntroAdsNativeVH(adsNativeBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val introModel: IntroModel = list[position]
        when (holder.itemViewType) {
            IntroType.GUIDE_1.ordinal, IntroType.GUIDE_2.ordinal, IntroType.GUIDE_3.ordinal, IntroType.GUIDE_4.ordinal -> {
                val viewHolderDefault = holder as IntroDefaultVH
                viewHolderDefault.binding.apply {
                    ivIntro.setImageResource(introModel.image)
                    tvTitle.setText(introModel.title)
                    tvContent.setText(introModel.content)
                }
            }

            IntroType.ADS.ordinal, IntroType.ADS_1.ordinal -> {
                val viewHolderAdsNative = holder as IntroAdsNativeVH
                viewHolderAdsNative.binding.ivClose.tap {
                    onCloseAds()
                }
                if (list[position].type == IntroType.ADS) {
                    showNativeFullScreen(nativeAdFullScreen, R.layout.ads_native_intro_full_screen, R.layout.ads_native_intro_full_screen, viewHolderAdsNative.binding.frAds, context, NATIVE_INTRO_FULL)
                    Handler(context.mainLooper).postDelayed({
                        showNativeFullScreen(nativeAdFullScreen2, R.layout.ads_native_intro_full_screen, R.layout.ads_native_intro_full_screen, viewHolderAdsNative.binding.frAds, context, NATIVE_INTRO_FULL_2)
                    }, 700)
                } else if (list[position].type == IntroType.ADS_1) {
                    showNativeFullScreen(nativeAdFullScreen1, R.layout.ads_native_intro_full_screen, R.layout.ads_native_intro_full_screen, viewHolderAdsNative.binding.frAds, context, NATIVE_INTRO_FULL1)
                    Handler(context.mainLooper).postDelayed({
                        showNativeFullScreen(nativeAdFullScreen12, R.layout.ads_native_intro_full_screen, R.layout.ads_native_intro_full_screen, viewHolderAdsNative.binding.frAds, context, NATIVE_INTRO_FULL1_2)
                    }, 700)
                }
            }
        }
    }

    private fun loadNativeIntroFull(adsKey: String, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey)) {
            Admob.getInstance().loadNativeAds(context, AdmobApi.getInstance().getListIDByName(adsKey), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    if (adsKey == NATIVE_INTRO_FULL) {
                        setNativeAdFullScreen(nativeAd)
                    } else if (adsKey == NATIVE_INTRO_FULL_2) {
                        setNativeAdFullScreen2(nativeAd)
                    }
                    notifyNativeAdFullScreen()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                    super.onAdFailedToLoad(loadAdError)
                    setNativeAdFullScreen(null)
                    setNativeAdFullScreen2(null)
                }
            }, remoteKey)
        }
    }

    private fun loadNativeIntroFull1(adsKey: String, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey)) {
            Admob.getInstance().loadNativeAds(context, AdmobApi.getInstance().getListIDByName(adsKey), object : NativeCallback() {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {
                    if (adsKey == NATIVE_INTRO_FULL1) {
                        setNativeAdFullScreen1(nativeAd)
                    } else if (adsKey == NATIVE_INTRO_FULL1_2) {
                        setNativeAdFullScreen12(nativeAd)
                    }
                    notifyNativeAdFullScreen1()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                    super.onAdFailedToLoad(loadAdError)
                    setNativeAdFullScreen1(null)
                    setNativeAdFullScreen12(null)
                }
            }, remoteKey)
        }
    }

    private fun showNativeFullScreen(nativeAd: NativeAd?, layoutNative: Int, layoutNativeMeta: Int, frAds: ViewGroup, context: Activity, remoteKey: String) {
        if (Admob.getInstance().checkCondition(context, remoteKey) && nativeAd != null) {
            val shimmer = LayoutInflater.from(context).inflate(R.layout.ads_shimmer_intro_full_screen, null)
            frAds.removeAllViews()
            frAds.addView(shimmer)
            val adView: NativeAdView
            var mediationAdapterClassName: String? = ""
            if (nativeAd.responseInfo != null) {
                mediationAdapterClassName = nativeAd.responseInfo?.mediationAdapterClassName
            }
            adView = if (mediationAdapterClassName != null && mediationAdapterClassName.lowercase(
                    Locale.getDefault()).contains("facebook")
            ) {
                context.layoutInflater.inflate(layoutNativeMeta, frAds, false) as NativeAdView
            } else {
                context.layoutInflater.inflate(layoutNative, frAds, false) as NativeAdView
            }
            frAds.removeAllViews()
            frAds.addView(adView)
            Admob.getInstance().populateNativeAdView(nativeAd, adView)
        }
    }

    fun setNativeAdFullScreen(nativeAd: NativeAd?) {
        this.nativeAdFullScreen = nativeAd
    }

    fun setNativeAdFullScreen2(nativeAd: NativeAd?) {
        this.nativeAdFullScreen2 = nativeAd
    }

    fun notifyNativeAdFullScreen() {
        notifyItemChanged(1)
    }

    fun setNativeAdFullScreen1(nativeAd: NativeAd?) {
        this.nativeAdFullScreen1 = nativeAd
    }

    fun setNativeAdFullScreen12(nativeAd: NativeAd?) {
        this.nativeAdFullScreen12 = nativeAd
    }

    fun notifyNativeAdFullScreen1() {
        list.forEachIndexed { index, introModel ->
            if (introModel.type == IntroType.ADS_1) {
                notifyItemChanged(index)
            }
        }
    }
}