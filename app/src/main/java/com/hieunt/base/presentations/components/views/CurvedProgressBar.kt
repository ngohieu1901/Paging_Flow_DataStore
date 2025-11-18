package com.hieunt.base.presentations.components.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import com.hieunt.base.R
import com.hieunt.base.widget.dpToPx
import com.hieunt.base.widget.toRadian
import kotlin.math.cos
import kotlin.math.sin


class CurvedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val centerX get() = width / 2f
    private val centerY get() = height / 2f

    private var _dotWidth = DOT_INDICATOR_WIDTH
    var dotWidth: Float
        get() = _dotWidth
        set(value) {
            _dotWidth = value
            paintDotIndicator.strokeWidth = _dotWidth
            paintDotIndicator.strokeWidth = _dotWidth
            invalidate()
        }

    private var _dotHeight = DOT_INDICATOR_HEIGHT
    var dotHeight: Float
        get() = _dotHeight
        set(value) {
            _dotHeight = value
            invalidate()
        }

    private var _borderSize = 0f
    var borderSize: Float
        get() = _borderSize
        set(value) {
            _borderSize = value
            invalidate()
        }

    private var _progress = 10
    var progress: Int
        get() = _progress
        set(value) {
            _progress = ((value / maxValue.toFloat()) * CONT_DOT_INDICATOR).toInt()
            invalidate()
        }
    private var _textSize = 10f.dpToPx(context)
    var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
            textPaint.textSize = textSize
            invalidate()
        }
    private var _typeface = ResourcesCompat.getFont(context, R.font.inter_regular)
    var typeface: Typeface?
        get() = _typeface
        set(value) {
            _typeface = value
            textPaint.typeface = typeface
            invalidate()
        }

    @ColorInt
    private var _textColor = Color.parseColor("#4DFFFFFF")
    var textColor: Int
        get() = _textColor
        set(@ColorInt value) {
            _textColor = value
            textPaint.color = textColor
            invalidate()
        }
    private var _maxValue = CONT_DOT_INDICATOR
    var maxValue: Int
        get() = _maxValue
        set(value) {
            _maxValue = value
            invalidate()
        }

    @ColorInt
    private var _colorDotBackground = Color.parseColor("#ffffff")
    var colorDotBackground: Int
        @ColorInt get() = _colorDotBackground
        set(@ColorInt value) {
            _colorDotBackground = value
            invalidate()
        }

    private val paintDotIndicator = Paint().apply {
        isAntiAlias = true
        color = colorDotBackground
        style = Paint.Style.STROKE
        strokeWidth = dotWidth
        strokeCap = Paint.Cap.BUTT
    }

    @ColorInt
    private var _colorDotProgress = Color.parseColor("#3A8DFF")
    var colorDotProgress: Int
        @ColorInt get() = _colorDotProgress
        set(@ColorInt value) {
            _colorDotProgress = value
            invalidate()
        }


    private val textPaint = Paint().also {
        it.textSize = this@CurvedProgressBar.textSize
        it.color = this@CurvedProgressBar.textColor
        it.typeface = this@CurvedProgressBar.typeface
    }

    init {
        obtainStyledAttributes(attrs, defStyleAttr)
    }

    private fun obtainStyledAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CurvedProgressBar,
            defStyleAttr,
            0
        )

        try {
            with(typedArray) {
                dotWidth = getDimension(
                    R.styleable.CurvedProgressBar_dotWidth,
                    dotWidth
                )
                borderSize = getDimension(
                    R.styleable.CurvedProgressBar_borderSize,
                    borderSize
                )
                dotHeight = getDimension(
                    R.styleable.CurvedProgressBar_dotHeight,
                    dotHeight
                )
                progress = getInt(
                    R.styleable.CurvedProgressBar_progress,
                    progress
                )
                maxValue = getInt(
                    R.styleable.CurvedProgressBar_maxValue,
                    maxValue
                )
                colorDotBackground = getColor(
                    R.styleable.CurvedProgressBar_colorDotBackground,
                    colorDotBackground
                )
                colorDotBackground = getColor(
                    R.styleable.CurvedProgressBar_colorDotBackground,
                    colorDotBackground
                )
                textColor = getColor(R.styleable.CurvedProgressBar_textColorProgress, textColor)
                textSize = getDimension(R.styleable.CurvedProgressBar_textSizeProgress, textSize)
                val textFont = getResourceId(R.styleable.CurvedProgressBar_textFontProgress, -1)
                if (textFont != -1) {
                    typeface = ResourcesCompat.getFont(context, textFont)
                }
            }
        } catch (e: Exception) {
            // ignored
        } finally {
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        renderDotIndicatorBackground(canvas)
    }

    private fun renderDotIndicatorBackground(canvas: Canvas) {
        for (p in 0..CONT_DOT_INDICATOR) {
            val startX = centerX + (centerX - dotWidth - dotHeight) * cos(mapProgressToAngle(p).toRadian())
            val startY = centerY - (centerY - dotWidth - dotHeight) * sin(mapProgressToAngle(p).toRadian())
            val stopX = centerX + (centerX - dotWidth) * cos(mapProgressToAngle(p).toRadian())
            val stopY = centerY - (centerY - dotWidth) * sin(mapProgressToAngle(p).toRadian())
            if (p >= (CONT_DOT_INDICATOR - progress))
                paintDotIndicator.color = colorDotProgress
            else
                paintDotIndicator.color = colorDotBackground

            if (p == CONT_DOT_INDICATOR / 2) {
                drawValueTextCenter(p, startX, stopX, startY, stopY, canvas)
            }
            if (p == 0) {
                drawValueTextEnd(p, startX, stopX, startY, stopY, canvas)
            }
            if (p == CONT_DOT_INDICATOR) {
                drawValueTextStart(p, startX, stopX, startY, stopY, canvas)
            }
            canvas.drawLine(startX, startY, stopX, stopY, paintDotIndicator)
        }
    }

    private fun drawValueTextCenter(p: Int, startX: Float, stopX: Float, startY: Float, stopY: Float, canvas: Canvas) {
        val value = ((CONT_DOT_INDICATOR - p * 1.0) / CONT_DOT_INDICATOR * maxValue).toInt()
        val textX = startX + (stopX - startX) / 2 - textPaint.measureText("$value") / 2
        val textY = startY + (stopY - startY) / 2 - (textPaint.descent() + textPaint.ascent()) / 2 - textSize + 60
        canvas.drawText("$value", textX, textY, textPaint)
    }

    private fun drawValueTextStart(p: Int, startX: Float, stopX: Float, startY: Float, stopY: Float, canvas: Canvas) {
        val value = ((CONT_DOT_INDICATOR - p * 1.0) / CONT_DOT_INDICATOR * maxValue).toInt()
        val textX = startX + (stopX - startX) / 2 + textPaint.measureText("$value") / 2
        val textY = startY + (stopY - startY) / 2 - (textPaint.descent() + textPaint.ascent()) / 2 - (textSize - 60) / 2
        canvas.drawText("$value", textX, textY, textPaint)
    }

    private fun drawValueTextEnd(p: Int, startX: Float, stopX: Float, startY: Float, stopY: Float, canvas: Canvas) {
        val value = ((CONT_DOT_INDICATOR - p * 1.0) / CONT_DOT_INDICATOR * maxValue).toInt()
        val textX = startX + (stopX - startX) / 2 - textPaint.measureText("$value") - 15
        val textY = startY + (stopY - startY) / 2 - (textPaint.descent() + textPaint.ascent()) / 2 - (textSize - 60) / 2
        canvas.drawText("$value", textX, textY, textPaint)
    }

    private fun mapProgressToAngle(progress: Int): Float {
        return (MIN_ANGLE + ((MAX_ANGLE - MIN_ANGLE) / (CONT_DOT_INDICATOR)) * (progress))
    }

    companion object {
        private const val CONT_DOT_INDICATOR = 100
        private const val MAX_ANGLE = 220f
        private const val MIN_ANGLE = -40f

        private const val DOT_INDICATOR_WIDTH = 5f
        private const val DOT_INDICATOR_HEIGHT = 10f
    }
}
