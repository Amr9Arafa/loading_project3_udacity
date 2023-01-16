package com.udacity

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progress = 0.0
    private val rectangleButtonPAint: Paint
    private val buttonTextPaint: Paint
    private val progressCirclePaint: Paint
    private var buttonText: String
    private val rect = RectF()
    private val boundOvalRect = Rect()


    private var backgroundCustomColor: Int
    private var textColor: Int

    private var valueAnimator = ValueAnimator()

    private fun setupAttr(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.LoadingButton,
            0, 0
        )

        backgroundCustomColor =
            typedArray.getColor(R.styleable.LoadingButton_backGroundColor, backgroundCustomColor)
        textColor = typedArray.getColor(R.styleable.LoadingButton_textCustomColor, textColor)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed)
    { p, old, new ->
        when (new) {
            ButtonState.Completed -> adjustCompletedState()
            ButtonState.Loading -> adjustLoadState()
//            ButtonState.Clicked -> adjustClickedState()


        }

    }

    private fun adjustLoadState() {
        rectangleButtonPAint.color = (context.getColor(R.color.colorPrimaryDark))
        buttonText = context.getString(R.string.button_loading)
    }

    private fun adjustClickedState() {
        rectangleButtonPAint.color = (context.getColor(R.color.colorPrimary))
        buttonText = context.getString(R.string.button_download)

    }

    private fun adjustCompletedState() {
        rectangleButtonPAint.color = backgroundCustomColor
        buttonText = context.getString(R.string.button_download)

    }


    fun onDownloadComplete() {
        valueAnimator.cancel()
        buttonState = ButtonState.Completed
        invalidate()
    }


    init {

        backgroundCustomColor = context.getColor(R.color.colorPrimary)
        textColor = Color.WHITE
        buttonText = context.getString(R.string.button_download)
        setupAttr(attrs)


        rectangleButtonPAint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                style = Paint.Style.FILL
                color = backgroundCustomColor
            }

        progressCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                style = Paint.Style.FILL
                color = context.getColor(R.color.colorAccent)
            }

        buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = 37.0f
                typeface = Typeface.create("", Typeface.BOLD)
                color = textColor
            }

        isClickable = true
        valueAnimator = AnimatorInflater.loadAnimator(
            context,
            R.animator.anim
        ) as ValueAnimator
        valueAnimator.addUpdateListener {
            progress = (it.animatedValue as Float).toDouble()
            invalidate()
            if (progress == 100.0) {
                onDownloadComplete()
            }
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed && MainActivity.isFileSelected)
            buttonState = ButtonState.Loading
        valueAnimator.start()
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val defaultButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                style = Paint.Style.FILL
                color = backgroundCustomColor
            }
        canvas?.drawRect(0.0f, 0.0f, width.toFloat(), height.toFloat(), defaultButtonPaint)
        canvas?.drawText(
            buttonText,
            (width / 2).toFloat(),
            ((height + 37) / 2).toFloat(),
            buttonTextPaint
        )
        if (buttonState == ButtonState.Loading) {
            canvas?.drawRect(
                0f, 0f,
                (widthSize * (progress / 100)).toFloat(), height.toFloat(), rectangleButtonPAint
            )

            progressCirclePaint.getTextBounds(buttonText, 0, buttonText.length, boundOvalRect)
            val centerX = measuredWidth.toFloat() / 2
            progressCirclePaint.color = context.getColor(R.color.colorAccent)
            rect.set(
                centerX + boundOvalRect.right / 2 + 90.0f,
                30.0f,
                centerX + boundOvalRect.right / 2 + 180.0f,
                measuredHeight.toFloat() - 35.0f
            )
            canvas?.drawArc(
                rect,
                0f, (360 * (progress / 100)).toFloat(),
                true,
                progressCirclePaint
            )
            canvas?.drawText(
                buttonText,
                (width / 2).toFloat(),
                ((height + 37) / 2).toFloat(),
                buttonTextPaint
            )
        }
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w
        heightSize = h
    }

}