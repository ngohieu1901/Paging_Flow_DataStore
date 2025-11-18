package com.hieunt.base.presentations.components.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import kotlin.math.abs


class RotationImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    private var rotationAnimator: ObjectAnimator? = null
    private var currentRotation = 0f

    private fun stopRotation() {
        currentRotation = rotation
        if (rotationAnimator?.isRunning == true) {
            rotationAnimator?.end()
        }
    }

    fun rotationTo(toDegree: Int) {
        stopRotation()
        rotationAnimator =
            ObjectAnimator.ofFloat(this, "rotation", currentRotation, toDegree.toFloat())
        rotationAnimator!!.setDuration((4000 * (abs(currentRotation - toDegree) / 360)).toLong())
        rotationAnimator!!.start()
    }

    fun azimuth(toDegree: Float) {
        rotation = toDegree.unaryMinus()
    }
}