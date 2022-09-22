package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new -> }

    private var value = 0f

    private var rectF = RectF(
        0.0f,
        0.0f,
        0.0f,
        0.0f
    )

    private fun RectF.computeLTRBLoadingState() {
        left = widthSize * (8.0f / 10.0f)
        top = heightSize * (1.0f / 10.0f)
        right = left + 80.0f
        bottom = top + 80.0f
    }

    private fun RectF.loadingRect() {
        left = 0.0f
        top = 0.0f
        right = value * widthSize.toFloat()
        bottom = heightSize.toFloat()
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    init {
        isClickable = true
    }

    override fun performClick(): Boolean {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener {
            value = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
                buttonState = ButtonState.Loading

            }
            override fun onAnimationEnd(animation: Animator?) {
                isEnabled = true
                buttonState = ButtonState.Completed
                value = 0f
            }
        })

        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = 1000
        valueAnimator.start()
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(
            context.getColor(R.color.colorPrimary)
        )
        paint.color = context.getColor(R.color.colorPrimaryDark)
        rectF.loadingRect()
        canvas?.drawRect(
            rectF,
            paint
        )
        paint.color = context.getColor(R.color.colorAccent)
        rectF.computeLTRBLoadingState()
        canvas?.drawArc(
            rectF, 0f, -1.0f * value * 360f, true, paint
        )
        paint.color = Color.BLACK
        drawText(canvas)
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.drawText(
            when (buttonState){
                ButtonState.Loading -> context.getString(R.string.button_loading)
                else -> context.getString(R.string.button_initial)
            },
            (canvas.width / 2.0f ),
            (canvas.height / 1.6f ),
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}