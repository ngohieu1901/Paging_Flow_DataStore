package com.hieunt.base.presentations.feature.screen_base.permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hieunt.base.R
import com.hieunt.base.base.BaseActivity
import com.hieunt.base.constants.Constants
import com.hieunt.base.databinding.ActivityPermissionBinding
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.firebase.ads.RemoteName
import com.hieunt.base.firebase.ads.RemoteName.NATIVE_PERMISSION
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.components.dialogs.WarningPermissionDialogFragment
import com.hieunt.base.presentations.feature.container.ContainerActivity
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.widget.callMultiplePermissions
import com.hieunt.base.widget.launchActivity
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {
    private val callStoragePermission = callMultiplePermissions {
        binding.ivSwitch.isChecked = true
        binding.ivSwitch.isEnabled = false
    }

    override fun initView() {
        logEvent(EventName.permission_open)
        loadNative(
            remoteKey = NATIVE_PERMISSION,
            remoteKeySecondary = NATIVE_PERMISSION,
            adsKeyMain = NATIVE_PERMISSION,
            adsKeySecondary = NATIVE_PERMISSION,
            idLayoutNative = R.layout.ads_native_large_button_above,
            idLayoutShimmer = R.layout.ads_shimmer_large_button_above
        )
        binding.tvContinue.text = getString(if (permissionUtils.isGrantAllFilesPermissionStorage()) R.string.tv_continue else R.string.skip)

        binding.ivSwitch.setOnCheckedChangeListener { _, isChecked ->
            logEvent(EventName.permission_allow_click)
            binding.ivSwitch.isEnabled = !isChecked
        }
        binding.ivSwitch.tap {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                AdsHelper.disableResume(this)
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            } else {
                if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.STORAGE_PERMISSION_API_SMALLER_30)) {
                    binding.ivSwitch.isChecked = false
                    WarningPermissionDialogFragment().show(supportFragmentManager, javaClass.name)
                } else {
                    callStoragePermission.launch(Constants.STORAGE_PERMISSION_API_SMALLER_30)
                }
            }
        }
        binding.tvContinue.tap {
            logEvent(EventName.permission_continue_click)
            SharePrefUtils(this).isPassPermission = true
            launchActivity(ContainerActivity::class.java)
            finishAffinity()
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                binding.ivSwitch.isChecked = permissionUtils.isGrantAllFilesPermissionStorage()
                binding.ivSwitch.isEnabled = !permissionUtils.isGrantAllFilesPermissionStorage()
                binding.tvContinue.text = getString(if (permissionUtils.isGrantAllFilesPermissionStorage()) R.string.tv_continue else R.string.skip)
            }
        }
    }

}