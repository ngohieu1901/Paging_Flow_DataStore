package com.hieunt.base.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
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
import com.hieunt.base.R
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.INTER_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_ALL
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_BANNER
import com.hieunt.base.utils.PermissionUtils
import com.hieunt.base.utils.SystemUtils.setLocale
import com.hieunt.base.widget.toast
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val permissionUtils by lazy { PermissionUtils(requireActivity())}

    val exceptionHandler: CoroutineExceptionHandler by lazy { CoroutineExceptionHandler { _, exception ->
        Log.e("CoroutineExceptionHandler1901", "${this::class.java.name}: ${exception.message}")
    } }

    protected abstract fun initData()
    protected abstract fun setupView()
    protected abstract fun dataCollect()

    open fun hideSoftKeyboard() {
        activity?.currentFocus?.let {
            val inputMethodManager =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    open fun handleOnBackPressed(): Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(setLocale(context))
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflate(
        inflater,
        container,
        false,
    ).also { _binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!this@BaseFragment.handleOnBackPressed()) {
                        popBackStack()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        setupView()
        dataCollect()
    }

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun safeNavigate(
        @IdRes resId: Int,
        args: Bundle? = null,
    ) {
        try {
            findNavControllerOrNull()?.navigate(resId, args)
        } catch (e: Exception) {
            Log.e("safeNavigate", "safeNavigate: $e")
        }
    }

    fun safeNavigate(
        navDirections: NavDirections
    ) {
        try {
            findNavControllerOrNull()?.navigate(navDirections)
        } catch (e: Exception) {
            Log.e("safeNavigate", "safeNavigate: $e")
        }
    }

    private fun findNavControllerOrNull(): NavController? {
        return try {
            findNavController()
        } catch (e: Exception) {
            null
        }
    }

    fun safeNavigateParentNav(
        navDirections: NavDirections
    ) {
        try {
            findParentNavController().navigate(navDirections)
        } catch (e: Exception) {
            Log.e("safeNavigate", "safeNavigate: $e")
        }
    }

    private fun findParentNavController(): NavController {
        return requireActivity().findNavController(R.id.fcv_app)
    }

    fun popBackStack(
        destinationId: Int? = null,
        inclusive: Boolean = false
    ) {
        findNavControllerOrNull()?.let {
            if (destinationId != null) {
                it.popBackStack(destinationId, inclusive)
            } else {
                it.popBackStack()
            }
        }
    }

    fun showPopupWindow(view: View, popupWindow: PopupWindow) {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val positionOfIcon = location[1]

        val displayMetrics = requireContext().resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3

        if (positionOfIcon > height) {
            popupWindow.showAsDropDown(view, -22, -(view.height * 7), Gravity.BOTTOM or Gravity.END)
        } else {
            popupWindow.showAsDropDown(view, -22, 0, Gravity.TOP or Gravity.END)
        }
    }

    private fun showLoading() {
        (activity as? BaseActivity<*>)?.showLoading()
    }

    private fun dismissLoading() {
        (activity as? BaseActivity<*>)?.dismissLoading()
    }

    fun renderStateLoading(isShowLoading: Boolean){
        if (isShowLoading) showLoading() else dismissLoading()
    }

    fun renderStateError(error: Throwable) {
        toast(error.message.toString())
    }

    protected fun loadBanner(adsKey: String) {
//        val banner = binding.root.findViewById<FrameLayout>(R.id.fr_banner)
//        if (banner != null) {
//            val bannerBuilder = BannerBuilder(requireActivity(), banner, true)
//            bannerBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKey))
//            val bannerManager = BannerManager(requireActivity(), viewLifecycleOwner, bannerBuilder, adsKey)
//            bannerManager.setAlwaysReloadOnResume(true)
//        }
    }

    protected fun loadNative(
        remoteKey: String,
        remoteKeySecondary: String,
        adsKeyMain: String,
        adsKeySecondary: String,
        idLayoutNative: Int,
        idLayoutShimmer: Int,
    ): NativeManager? {
        val frAds = binding.root.findViewById<FrameLayout>(R.id.fr_ads)
        if (frAds != null) {
            val nativeBuilder = NativeBuilder(requireContext(), frAds, idLayoutShimmer, idLayoutNative, idLayoutNative, true)
            nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(adsKeyMain))
            nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(adsKeySecondary))
            val nativeManager = NativeManager(requireContext(), viewLifecycleOwner, nativeBuilder, remoteKey, remoteKeySecondary)
            nativeManager.timeOutCallAds = 12000
            nativeManager.setIntervalReloadNative(
                RemoteConfigHelper.getInstance().get_config_long(requireContext(), RemoteConfigHelper.interval_reload_native) * 1000,
            )
            nativeManager.setAlwaysReloadOnResume(true)
            return nativeManager
        } else {
            return null
        }
    }

    protected fun loadNativeAll(): NativeManager? {
        val frAds = binding.root.findViewById<FrameLayout>(R.id.fr_ads)
        if (frAds != null) {
            val nativeBuilder = NativeBuilder(requireContext(), frAds, R.layout.ads_shimmer_large_button_above, R.layout.ads_native_large_button_above,R.layout.ads_native_large_button_above, true)
            nativeBuilder.setListIdAdMain(AdmobApi.getInstance().getListIDByName(NATIVE_ALL))
            nativeBuilder.setListIdAdSecondary(AdmobApi.getInstance().getListIDByName(NATIVE_ALL))
            val nativeManager = NativeManager(requireContext(), viewLifecycleOwner, nativeBuilder, NATIVE_ALL, NATIVE_ALL)
            nativeManager.timeOutCallAds = 12000
            nativeManager.setIntervalReloadNative(
                RemoteConfigHelper.getInstance().get_config_long(requireContext(), RemoteConfigHelper.interval_reload_native) * 1000,
            )
            nativeManager.setAlwaysReloadOnResume(true)
            return nativeManager
        } else {
            return null
        }
    }

    fun loadAndShowInter(
        adsKey: String,
        remoteKey: String,
        onNextAction: () -> Unit,
    ) {
        InterManager.loadAndShowInterAds(
            requireActivity(),
            adsKey,
            remoteKey,
            object : InterCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    onNextAction.invoke()
                }

                override fun onAdFailedToShowFullScreenContent() {
                    super.onAdFailedToShowFullScreenContent()
                    setIntervalInterAll(adsKey = adsKey)
                }

                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    setIntervalInterAll(adsKey = adsKey)
                }
            },
        )
    }

    fun loadAndShowInterAll(
        onNextAction: () -> Unit,
    ) {
        InterManager.loadAndShowInterAds(
            requireActivity(),
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
        val intervalInterAll = RemoteConfigHelper.getInstance().get_config_long(requireContext(), RemoteName.INTERVAL_INTER_ALL)
        if (adsKey == INTER_ALL && intervalInterAll > 0) {
            Admob.getInstance().setTimeInterval(intervalInterAll * 1000, false)
        }
    }

    fun showReward(
        adsKey: String,
        remoteKey: String,
        isReloadAfterShow: Boolean,
        onNextAction: () -> Unit,
    ) {
        var earnedReward = false
        RewardManager.showRewardAds(
            requireActivity(),
            adsKey,
            remoteKey,
            object : RewardedCallback() {
                override fun onUserEarnedReward() {
                    super.onUserEarnedReward()
                    earnedReward = true
                }

                override fun onNextAction() {
                    super.onNextAction()
                    if (earnedReward) {
                        onNextAction.invoke()
                    }
                }
            },
            isReloadAfterShow,
        )
    }

    protected fun loadCollapseBanner(remoteKey: String): CollapseBannerManager? {
        val frContainerAds = binding.root.findViewById<FrameLayout>(R.id.collapsible_banner_container_view)
        if (frContainerAds != null) {
            val collapseBannerBuilder = CollapseBannerBuilder()
            collapseBannerBuilder.setListId(AdmobApi.getInstance().getListIDByName(RemoteName.COLLAPSE_BANNER))
            val collapseBannerManager = CollapseBannerManager(requireActivity() as AppCompatActivity, frContainerAds, viewLifecycleOwner, collapseBannerBuilder, remoteKey)
            collapseBannerManager.setIntervalReloadBanner(
                RemoteConfigHelper.getInstance().get_config_long(requireContext(), RemoteName.COLLAPSE_RELOAD_INTERVAL) * 1000
            )
            collapseBannerManager.setAlwaysReloadOnResume(true)
            return collapseBannerManager
        }
        return null
    }

    protected fun loadCollapseBanner(adsKey: String, remoteKey: String): CollapseBannerManager? {
        val frContainerAds = binding.root.findViewById<FrameLayout>(R.id.collapsible_banner_container_view)
        if (frContainerAds != null) {
            val collapseBannerBuilder = CollapseBannerBuilder()
            collapseBannerBuilder.setListId(AdmobApi.getInstance().getListIDByName(adsKey))
            val collapseBannerManager = CollapseBannerManager(requireActivity() as AppCompatActivity, frContainerAds, viewLifecycleOwner, collapseBannerBuilder, remoteKey)
            collapseBannerManager.setIntervalReloadBanner(
                RemoteConfigHelper.getInstance().get_config_long(requireContext(), RemoteName.COLLAPSE_RELOAD_INTERVAL) * 1000
            )
            collapseBannerManager.setAlwaysReloadOnResume(true)
            return collapseBannerManager
        } else {
            return null
        }
    }

    protected fun loadNativeBanner(remoteKey: String): CollapseBannerManager? {
        val testAdsBanner = RemoteConfigHelper.getInstance().get_config(requireContext(), RemoteName.TEST_ADS_BANNER)
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

}