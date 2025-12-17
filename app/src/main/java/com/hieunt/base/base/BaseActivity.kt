package com.hieunt.base.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.hieunt.base.constants.Constants.IntentKeys.SCREEN
import com.hieunt.base.constants.Constants.IntentKeys.SPLASH_ACTIVITY
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.INTER_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_BANNER
import com.hieunt.base.presentations.components.dialogs.LoadingDialog
import com.hieunt.base.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.appUpdateManager
import com.hieunt.base.presentations.feature.screen_base.splash.SplashActivity.Companion.installStateUpdatedListener
import com.hieunt.base.utils.PermissionUtils
import com.hieunt.base.utils.SystemUtils.setLocale
import com.hieunt.base.widget.currentBundle
import com.hieunt.base.widget.hideNavigation
import com.hieunt.base.widget.hideStatusBar
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.toast
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB,
) : AppCompatActivity() {
    protected lateinit var binding: VB
    private var isRegistered = false
    private var networkCallback: NetworkCallbackHandler? = null

    protected val permissionUtils by lazy { PermissionUtils(this) }

    protected val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("CoroutineExceptionHandler1901", "${this::class.java.name}: ${exception.message}")
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
        //internet
        networkCallback = NetworkCallbackHandler {
            if (!it) {
                if (this !is NoInternetActivity) {
                    launchActivity(NoInternetActivity::class.java)
                }
            } else {
                if (this is NoInternetActivity && this.currentBundle()
                        ?.getString(SCREEN) != SPLASH_ACTIVITY
                ) {
                    finish()
                } else if (this is NoInternetActivity && this.currentBundle()
                        ?.getString(SCREEN) == SPLASH_ACTIVITY
                ) {
                    val myIntent = Intent(this, SplashActivity::class.java)
                    myIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(myIntent)
                    finishAffinity()
                }
            }
        }
        networkCallback?.register(this)
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
}