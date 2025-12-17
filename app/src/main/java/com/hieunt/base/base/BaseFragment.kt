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
                    if (this@BaseFragment.handleOnBackPressed()) return
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
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

    fun safeNavigate(navDirections: NavDirections) {
        try {
            findNavControllerOrNull()?.navigate(navDirections)
        } catch (e: IllegalArgumentException) {
            Log.d("safeNavigateException", "safeNavigate: $e")
        }
    }

    private fun findNavControllerOrNull(): NavController? {
        return try {
            findNavController()
        } catch (_: Exception) {
            null
        }
    }

    fun safeNavigateParentNav(navDirections: NavDirections) {
        try {
            findParentNavController()?.navigate(navDirections)
        } catch (e: IllegalArgumentException) {
            Log.e("safeNavigateException", "safeNavigateParentNav: $e")
        }
    }

    private fun findParentNavController(): NavController? {
        return try {
            requireActivity().findNavController(R.id.fcv_app)
        } catch (e: IllegalStateException) {
            Log.e("findNavException", "safeNavigateParentNav: $e")
            null
        }
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

    protected fun showLoading() {
        (activity as? BaseActivity<*>)?.showLoading()
    }

    protected fun dismissLoading() {
        (activity as? BaseActivity<*>)?.dismissLoading()
    }

    fun renderStateLoading(isShowLoading: Boolean){
        if (isShowLoading) showLoading() else dismissLoading()
    }

    fun renderStateError(error: Throwable) {
        toast(error.message.toString())
    }
}