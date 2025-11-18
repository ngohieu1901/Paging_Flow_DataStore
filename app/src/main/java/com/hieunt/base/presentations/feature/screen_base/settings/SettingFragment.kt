package com.hieunt.base.presentations.feature.screen_base.settings

import android.content.Intent
import androidx.core.net.toUri
import com.hieunt.base.R
import com.hieunt.base.base.BaseFragment
import com.hieunt.base.constants.Constants
import com.hieunt.base.databinding.FragmentSettingsBinding
import com.hieunt.base.firebase.ads.AdsHelper
import com.hieunt.base.firebase.event.EventName
import com.hieunt.base.presentations.components.dialogs.RatingDialogFragment
import com.hieunt.base.presentations.feature.main.MainFragmentDirections
import com.hieunt.base.utils.SharePrefUtils
import com.hieunt.base.widget.gone
import com.hieunt.base.widget.launchAndRepeatWhenViewStarted
import com.hieunt.base.widget.logEvent
import com.hieunt.base.widget.tap
import com.hieunt.base.widget.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment: BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    @Inject
    lateinit var sharePref: SharePrefUtils

    override fun initData() {
        binding.apply {
            llLanguage.tap {
                logEvent(EventName.setting_language_click)
                safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToLanguageFragment())
            }
            llShare.tap {
                logEvent(EventName.setting_share_click)
                AdsHelper.disableResume(requireActivity())
                val intentShare = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, """     ${getString(R.string.app_name)}  https://play.google.com/store/apps/details?id=${requireActivity().packageName}  """.trimIndent())
                }
                startActivity(Intent.createChooser(intentShare, "Share"))
            }
            llRate.tap {
                if (!sharePref.isRated) {
                    val rateDialog = RatingDialogFragment(isFinishActivity = false, onClickRate = {
                        llRate.gone()
                    })
                    rateDialog.show(childFragmentManager, "RatingDialog")
                }
            }
            llPolicy.tap {
                logEvent(EventName.setting_privacy_policy_click)
                AdsHelper.disableResume(requireActivity())
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Constants.PRIVACY_POLICY.toUri()
                )
                startActivity(browserIntent)
            }
        }
    }

    override fun setupView() {

    }

    override fun dataCollect() {
        launchAndRepeatWhenViewStarted({
            binding.apply {
                if (sharePref.isRated) {
                    llRate.gone()
                } else {
                    llRate.visible()
                }
            }
        })
    }
}