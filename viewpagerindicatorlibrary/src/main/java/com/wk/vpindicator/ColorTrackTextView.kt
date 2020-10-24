package com.wk.vpindicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ColorTrackTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // 当前文本
    private var mText: String = ""

    // 当前进度
    private var mCurrentProgress = 0.6f

    // 文本默认颜色
    private var mOriginColor = Color.BLACK

    // 文本改变的颜色
    private var mChangeColor = Color.RED

    // 文本默认画笔
    private val mOriginPaint: Paint by lazy { Paint() }

    // 文本改变颜色的画笔
    private val mChangePaint: Paint by lazy { Paint() }

    private var mDirection =
        Direction.DIRECTION_LEFT

    enum class Direction {
        DIRECTION_LEFT, DIRECTION_RIGHT
    }

    init {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView)
        mOriginColor = typedArray.getColor(R.styleable.ColorTrackTextView_origin_color, Color.BLACK)
        mChangeColor = typedArray.getColor(R.styleable.ColorTrackTextView_change_color, Color.RED)
        typedArray.recycle()

        initPaint(mOriginColor, mOriginPaint)
        initPaint(mChangeColor, mChangePaint)
    }

    fun setCurrentProcess(process: Float) {
        mCurrentProgress = process
        invalidate()
    }

    fun setDirection(direction: Direction) {
        mDirection = direction
    }

    /**
     * 初始化画笔参数
     */
    private fun initPaint(color: Int, paint: Paint) {
        paint.color = color

        paint.isAntiAlias = true
        paint.isDither = true
        paint.textSize = textSize
    }


    override fun onDraw(canvas: Canvas?) {
        mText = text.toString()

        mText.takeIf { it.isNotEmpty() }
            ?.let {
                val middle = (width * mCurrentProgress).toInt()
                if (mDirection == Direction.DIRECTION_LEFT) {
                    drawOriginDirectionLeft(canvas, middle)
                    drawChangeDirectionLeft(canvas, middle)
                }

                if (mDirection == Direction.DIRECTION_RIGHT) {
                    drawOriginDirectionRight(canvas, middle)
                    drawChangeDirectionRight(canvas, middle)
                }
            }
    }

    /**
     * 绘制右侧变色的文字
     */
    private fun drawChangeDirectionRight(canvas: Canvas?, middle: Int) {
        drawText(canvas, mChangePaint, width - middle, width)
    }

    /**
     * 绘制右侧默认色的文字
     */
    private fun drawOriginDirectionRight(canvas: Canvas?, middle: Int) {
        drawText(canvas, mOriginPaint, 0, width - middle)
    }

    /**
     * 绘制左侧变色的文字
     */
    private fun drawChangeDirectionLeft(canvas: Canvas?, middle: Int) {
        drawText(canvas, mChangePaint, 0, middle)
    }

    /**
     * 绘制左侧默认色的文字
     */
    private fun drawOriginDirectionLeft(canvas: Canvas?, middle: Int) {
        drawText(canvas, mOriginPaint, middle, width)
    }

    /**
     * 绘制文本
     */
    private fun drawText(canvas: Canvas?, paint: Paint, startX: Int, endX: Int) {
        canvas?.let {
            // 保存画笔状态
            it.save()
            //截取绘制的内容(只会绘制设置的参数部分)
            it.clipRect(startX, 0, endX, height)
            val bounds = Rect()
            paint.getTextBounds(mText, 0, mText.length, bounds)
            // 获取文字的Metrics 用来计算基线
            val fontMetrics = paint.fontMetricsInt
            // 计算基线的位置
            val baseLine = (height - fontMetrics.descent).toFloat()
            canvas.drawText(mText, measuredWidth / 2f - bounds.width() / 2f, baseLine, paint)
            // 释放画笔状态
            it.restore()
        }
    }

}