package com.hieunt.base.widget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hieunt.base.firebase.event.AdmobEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//start activity
fun Fragment.launchActivity(
    clazz: Class<*>
) {
    startActivity(Intent(context, clazz))
}

fun Fragment.launchActivity(
    option: Bundle? = null,
    clazz: Class<*>
) {
    val intent = Intent(context, clazz)
    intent.putExtra("data_bundle", option)
    startActivity(intent)
}

fun Fragment.finishActivity() {
    activity?.finish()
}

fun Fragment.finishAffinity() {
    activity?.finishAffinity()
}

fun Fragment.currentBundle(): Bundle? {
    return activity?.intent?.getBundleExtra("data_bundle")
}

fun Fragment.launchActivityForResult(
    callback: (Boolean) -> Unit
) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        callback.invoke(result.resultCode == AppCompatActivity.RESULT_OK)
    }
}

fun Fragment.toast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.findParentNavController(): NavController? {
    var parent = parentFragment
    while (parent != null && parent !is NavHostFragment) {
        parent = parent.parentFragment
    }
    return parent?.parentFragment?.findNavController()
}

fun Fragment.launchAndRepeatWhenViewStarted(
    launchBlock: suspend () -> Unit,
    vararg launchBlocks: suspend () -> Unit,
): Job =
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            launch { launchBlock() }
            launchBlocks.forEach { launch { it() } }
        }
    }

fun <T> Fragment.collectLatestLifecycleFlow(
    flow: Flow<T>,
    action: suspend (T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(action)
        }
    }
}

fun Fragment.callMultiplePermissions(
    callbackPermission: (Boolean) -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { callback ->
        callbackPermission.invoke(!callback.containsValue(false))
    }
}

fun Fragment.logEvent(nameEvent: String, bundle: Bundle = Bundle()) {
    AdmobEvent.logEvent(requireContext(), nameEvent, bundle)
}
