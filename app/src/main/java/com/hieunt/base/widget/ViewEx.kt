package com.hieunt.base.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide

fun ImageView.setImageWithFade(@DrawableRes resId: Int, duration: Long = 300L) {
    val fadeOut = ObjectAnimator.ofFloat(this, View.ALPHA, 1f, 0f).setDuration(duration / 2)
    val fadeIn = ObjectAnimator.ofFloat(this, View.ALPHA, 0f, 1f).setDuration(duration / 2)

    fadeOut.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            setImageResource(resId)
            fadeIn.start()
        }
    })

    fadeOut.start()
}

fun ConstraintLayout.fadeBackgroundTo(@DrawableRes newDrawableRes: Int) {
    val context = this.context
    val currentBackground = this.background ?: ColorDrawable(Color.TRANSPARENT)
    val newDrawable = ContextCompat.getDrawable(context, newDrawableRes)

    if (newDrawable != null) {
        val transition = TransitionDrawable(arrayOf(currentBackground, newDrawable)).apply {
            isCrossFadeEnabled = true
        }
        this.background = transition
        transition.startTransition(400)
    }
}


fun ConstraintLayout.slideInBackground(@DrawableRes newDrawableRes: Int) {
    val newDrawable = ContextCompat.getDrawable(context, newDrawableRes) ?: return

    val imageView = AppCompatImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageDrawable(newDrawable)
        translationX = width.toFloat()
        alpha = 0f
    }

    this.addView(imageView, 0)

    imageView.animate()
        .translationX(0f)
        .alpha(1f)
        .setDuration(500)
        .withEndAction {
            this.background = newDrawable
            this.removeView(imageView)
        }
        .start()
}

fun ConstraintLayout.animatedBackgroundChange(@DrawableRes newDrawableRes: Int) {
    val newDrawable = ContextCompat.getDrawable(context, newDrawableRes) ?: return

    val imageView = AppCompatImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        scaleType = ImageView.ScaleType.CENTER_CROP
        setImageDrawable(newDrawable)
        alpha = 0f
        scaleX = 1.1f
        scaleY = 1.1f
    }

    this.addView(imageView, 0)

    imageView.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(500)
        .withEndAction {
            this.background = newDrawable
            this.removeView(imageView)
        }
        .start()
}



fun View.fadeAndSlideIn(
    duration: Long = 350,
    slideDistance: Float = 30f, // dp
    fromBottom: Boolean = true
) {
    val context = this.context
    val distancePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        slideDistance,
        context.resources.displayMetrics
    )

    this.apply {
        alpha = 0f
        translationY = if (fromBottom) distancePx else -distancePx
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .translationY(0f)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(duration)
            .start()
    }
}

fun View.fadeAndSlideOut(
    duration: Long = 350,
    slideDistance: Float = 30f,
    toBottom: Boolean = true,
    onEnd: (() -> Unit)? = null
) {
    val context = this.context
    val distancePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        slideDistance,
        context.resources.displayMetrics
    )

    this.animate()
        .alpha(0f)
        .translationY(if (toBottom) distancePx else -distancePx)
        .setInterpolator(AccelerateInterpolator())
        .setDuration(duration)
        .withEndAction {
            this.visibility = View.GONE
            onEnd?.invoke()
        }
        .start()
}

fun View.layoutInflate(): LayoutInflater = LayoutInflater.from(context)

fun View.tap(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 1500)
        action(it)
    }
}

fun View.tapShort(action: (view: View?) -> Unit) {
    setOnClickListener {
        it.isEnabled = false
        it.postDelayed({ it.isEnabled = true }, 500)
        action(it)
    }
}

fun View.hideKeyboard() {
    val context = this.context ?: return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.setHeight(newHeight: Int) {
    layoutParams.height = newHeight
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.enable() {
    isEnabled = true
    isClickable = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    isClickable = false
    alpha = 0.4f
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun EditText.getTextEx(): String = text.toString().trim()

fun loadImage(view: ImageView, url: String) {
    Glide.with(view.context).load(url).into(view)
}
