package com.hieunt.base.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.Admob
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.banner_ads.BannerBuilder
import com.amazic.library.ads.banner_ads.BannerManager
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.RewardedCallback
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerBuilder
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerManager
import com.amazic.library.ads.inter_ads.InterManager
import com.amazic.library.ads.native_ads.NativeBuilder
import com.amazic.library.ads.native_ads.NativeManager
import com.amazic.library.ads.reward_ads.RewardManager
import com.google.android.play.core.install.model.InstallStatus
import com.hieunt.base.R
import com.hieunt.base.base.network.NetworkCallbackHandler
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.INTER_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_BANNER
import com.hieunt.base.presentations.components.dialogs.LoadingDialog
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.appUpdateManager
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.installStateUpdatedListener
import com.hieunt.base.utils.PermissionUtils
import com.hieunt.base.utils.SystemUtils.setLocale
import com.hieunt.base.widget.hideNavigation
import com.hieunt.base.widget.hideStatusBar
import com.hieunt.base.widget.toast
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB,
) : AppCompatActivity() {    protected lateinit var binding: VB
    private var isRegistered = false
    private var networkCallback: NetworkCallbackHandler? = null

    protected val permissionUtils by lazy { PermissionUtils(this) }

    protected val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("CoroutineExceptionHandler1901", "${this::class.java.name}: ${exception.message}")
        }
    }

    private val backPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this@BaseActivity.handleOnBackPressed()
            }
        }

    private val loadingDialog by lazy { LoadingDialog(this) }
    protected abstract fun initView()
    protected abstract fun dataCollect()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.hideNavigation()
        window.hideStatusBar()
        super.onCreate(savedInstanceState)
        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        //onBackPress
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        //internet
//        networkCallback = NetworkCallbackHandler {
//            if (!it) {
//                if (this !is NoInternetActivity) {
//                    launchActivity(NoInternetActivity::class.java)
//                }
//            } else {
//                if (this is NoInternetActivity && this.currentBundle()
//                        ?.getString(SCREEN) != SPLASH_ACTIVITY
//                ) {
//                    finish()
//                } else if (this is NoInternetActivity && this.currentBundle()
//                        ?.getString(SCREEN) == SPLASH_ACTIVITY
//                ) {
//                    val myIntent = Intent(this, SplashActivity::class.java)
//                    myIntent.flags =
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(myIntent)
//                    finishAffinity()
//                }
//            }
//        }
//        networkCallback?.register(this)
        initView()
        dataCollect()
    }

    override fun onResume() {
        super.onResume()
        window.hideStatusBar()
        window.hideNavigation()
        AdsHelper.enableResume(this)
        installStateUpdatedListener?.let { appUpdateManager?.registerListener(it) }
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            // If the update is downloaded but not installed,
            // notify the user to complete the update.
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager?.completeUpdate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback?.unregister()
        installStateUpdatedListener?.let { appUpdateManager?.unregisterListener(it) }
    }

    open fun handleOnBackPressed() {
        backPressedCallback.isEnabled = false
        onBackPressedDispatcher.onBackPressed()
        backPressedCallback.isEnabled = true
    }


    protected fun showPopupWindow(view: View, popupWindow: PopupWindow) {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val positionOfIcon = location[1]

        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3

        if (positionOfIcon > height) {
            popupWindow.showAsDropDown(view, -22, -(view.height * 7), Gravity.BOTTOM or Gravity.END)
        } else {
            popupWindow.showAsDropDown(view, -22, 0, Gravity.TOP or Gravity.END)
        }
    }

    fun showLoading() {
        if (loadingDialog.isShowing.not())
            loadingDialog.show()
    }

    fun dismissLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()
    }

    fun renderStateLoading(isShowLoading: Boolean) {
        if (isShowLoading) showLoading() else dismissLoading()
    }

    fun renderStateError(error: Throwable) {
        toast(error.message.toString())
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { setLocale(it) })
    }

    fun loadCollapseBanner(remoteKey: String): CollapseBannerManager?  {
        val frContainerAds = findViewById<FrameLayout>(R.id.collapsible_banner_container_view)
        if (frContainerAds != null) {
            val collapseBannerBuilder = CollapseBannerBuilder()
            collapseBannerBuilder.setListId(AdmobApi.getInstance().getListIDByName(RemoteName.COLLAPSE_BANNER))
            val collapseBannerManager = CollapseBannerManager(this, frContainerAds, this, collapseBannerBuilder, remoteKey)
            collapseBannerManager.setIntervalReloadBanner(
                RemoteConfigHelper.getInstance().get_config_long(this, RemoteName.COLLAPSE_RELOAD_INTERVAL) * 1000
            )
            collapseBannerManager.setAlwaysReloadOnResume(true)
            return collapseBannerManager
        }
        return null
    }

    protected fun loadBanner(adsKey: String) {
//        val banner = findViewById<FrameLayout>(R.id.banner_container_view)
//        if (banner != null) {
//            val bannerBuilder = BannerBuilder(this, frAds, true)
//            bannerBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
//            val bannerManager = BannerManager(this, this, bannerBuilder, adsKey)
//            bannerManager.setAlwaysReloadOnResume(true)
//            //if load multiple id native
//            /*val bannerBuilder = BannerBuilder(this, frAds, true)
//            bannerBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
//            bannerBuilder.setListIdAdSecondary()
//            bannerBuilder.setListIdAdBackup()
//            val bannerManager = BannerManager(this, this, bannerBuilder, adsKey)
//            bannerManager.remoteKeySecondary = ""
//            bannerManager.remoteKeyBackup = ""
//            bannerManager.setAlwaysReloadOnResume(true)*/
//        }
    }

    protected fun loadNative(
        remoteKey: String,
        remoteKeySecondary: String,
        adsKeyMain: String,
        adsKeySecondary: String,
        idLayoutNative: Int,
        idLayoutShimmer: Int,
        idNativeMeta: Int = idLayoutNative,
        isAlwaysReloadOnResume: Boolean = true,
    ): NativeManager? {
        val frAds = findViewById<FrameLayout>(R.id.fr_ads)
        if (frAds != null) {
            val nativeBuilder = NativeBuilder(this, frAds, idLayoutShimmer, idLayoutNative, idNativeMeta, true)
            nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKeyMain))
            // set secondary list id ads if load double native
            nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKeySecondary))
            // set backup list id ads if need load backup ads when loading ads fail
//            nativeBuilder.setListIdAdBackup(AdmobApi.getInstance().getListIDByName(adsKeyBackup))
            val nativeManager = NativeManager(this, this, nativeBuilder, remoteKey, remoteKeySecondary)
//            nativeManager.remoteKeyBackup = adsKeyBackup
            nativeManager.timeOutCallAds = 12000
            nativeManager.setIntervalReloadNative(
                RemoteConfigHelper.getInstance().get_config_long(this, RemoteConfigHelper.interval_reload_native) * 1000,
            )
            nativeManager.setAlwaysReloadOnResume(isAlwaysReloadOnResume)
            return nativeManager
        } else {
            return null
        }
    }

    protected fun loadNativeBanner(remoteKey: String): CollapseBannerManager? {
        val testAdsBanner = RemoteConfigHelper.getInstance().get_config(this, RemoteName.TEST_ADS_BANNER)
        /*
            testAdsBanner = true -> native
            testAdsBanner = false -> collapse
         */
        if (testAdsBanner) {
            loadNative(
                NATIVE_BANNER,
                NATIVE_BANNER,
                NATIVE_BANNER,
                NATIVE_BANNER,
                R.layout.native_meta_small_with_button_below,
                R.layout.shimmer_native_meta_small_with_button_below,
            )
            return null
        } else {
            return loadCollapseBanner(remoteKey = remoteKey)
        }
    }

    protected fun loadAndShowInter(
        adsKey: String,
        remoteKey: String,
        onNextAction: () -> Unit,
    ) {
        InterManager.loadAndShowInterAds(
            this,
            adsKey,
            remoteKey,
            object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction.invoke()
                }
            },
        )
    }

    protected fun loadAndShowInter(
        adsKey: String,
        onNextAction: () -> Unit,
    ) {
        InterManager.loadAndShowInterAds(
            this,
            adsKey,
            adsKey,
            object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction.invoke()
                }
            },
        )
    }

    protected fun loadReward(adsKey: String?) {
        RewardManager.loadRewardAds(this, adsKey, adsKey)
    }

    protected fun loadReward(
        adsKey: String?,
        remoteKey: String?,
    ) {
        RewardManager.loadRewardAds(this, adsKey, remoteKey)
    }

    protected fun showReward(
        adsKey: String?,
        isReloadAds: Boolean,
        onNextAction: () -> Unit,
    ) {
        RewardManager.showRewardAds(
            this,
            adsKey,
            adsKey,
            object : RewardedCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction()
                }
            },
            isReloadAds,
        )
    }

    protected fun showReward(
        adsKey: String?,
        remoteKey: String?,
        isReloadAds: Boolean,
        onNextAction: () -> Unit,
    ) {
        RewardManager.showRewardAds(
            this,
            adsKey,
            remoteKey,
            object : RewardedCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction()
                }
            },
            isReloadAds,
        )
    }

    protected fun loadNativeAll(): NativeManager? {
        val frAds = binding.root.findViewById<FrameLayout>(R.id.fr_ads)
        if (frAds != null) {
            val nativeBuilder = NativeBuilder(this, frAds, R.layout.ads_shimmer_large_button_above, R.layout.ads_native_large_button_above,R.layout.ads_native_large_button_above, true)
            nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(NATIVE_ALL))
            nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(NATIVE_ALL))
            val nativeManager = NativeManager(this, this, nativeBuilder, NATIVE_ALL, NATIVE_ALL)
            nativeManager.timeOutCallAds = 12000
            nativeManager.setIntervalReloadNative(
                RemoteConfigHelper.getInstance().get_config_long(this, RemoteConfigHelper.interval_reload_native) * 1000,
            )
            nativeManager.setAlwaysReloadOnResume(true)
            return nativeManager
        } else {
            return null
        }
    }

    protected fun loadAndShowInterAll(
        onNextAction: () -> Unit,
    ) {
        InterManager.loadAndShowInterAds(
            this,
            INTER_ALL,
            INTER_ALL,
            object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction.invoke()
                }

                override fun onAdFailedToShowFullScreenContent() {
                    super.onAdFailedToShowFullScreenContent()
                    setIntervalInterAll(adsKey = INTER_ALL)
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    setIntervalInterAll(adsKey = INTER_ALL)
                }
            },
        )
    }

    private fun setIntervalInterAll(adsKey: String) {
        val intervalInterAll = RemoteConfigHelper.getInstance().get_config_long(this, RemoteName.INTERVAL_INTER_ALL)
        if (adsKey == INTER_ALL && intervalInterAll > 0) {
            Admob.getInstance().setTimeInterval(intervalInterAll * 1000, false)
        }
    }
}