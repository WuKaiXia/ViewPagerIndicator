package com.wk.vpindicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Email 240336124@qq.com
 * Created by Darren on 2016/12/11.
 * Version 1.0
 * Description:  颜色跟踪的TextView
 */
class ColorTrackTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {
    // 1. 实现一个文字两种颜色 - 绘制不变色字体的画笔
    private var mOriginPaint: Paint? = null

    // 1. 实现一个文字两种颜色 - 绘制变色字体的画笔
    private var mChangePaint: Paint? = null

    // 1. 实现一个文字两种颜色 - 当前的进度
    private var mCurrentProgress = 0f

    // 2. 实现两种朝向 - 当前的朝向  从左到右还是从右到左
    private var mDirection = Direction.LEFT_TO_RIGHT

    enum class Direction {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    /**
     * 1.1 初始化画笔
     */
    private fun initPaint(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView)
        val originColor = array.getColor(R.styleable.ColorTrackTextView_origin_color, textColors.defaultColor)
        val changeColor = array.getColor(R.styleable.ColorTrackTextView_change_color, textColors.defaultColor)
        mOriginPaint = getPaintByColor(originColor)
        mChangePaint = getPaintByColor(changeColor)

        // 回收
        array.recycle()
    }

    /**
     * 1.根据颜色获取画笔
     */
    private fun getPaintByColor(color: Int): Paint {
        val paint = Paint()
        // 设置颜色
        paint.color = color
        // 设置抗锯齿
        paint.isAntiAlias = true
        // 防抖动
        paint.isDither = true
        // 设置字体的大小  就是TextView的字体大小
        paint.textSize = textSize
        return paint
    }

    override fun onDraw(canvas: Canvas) {
        // 不能使用系统的
        // super.onDraw(canvas);

        // 1.2 实现不同的颜色
        // 1.2.1 计算中间的位置  = 当前的进度 * 控件的宽度
        val middle = (mCurrentProgress * width).toInt()
        // 1.2.3 根据中的位置去绘制  两边不同的文字颜色  截取绘制文字的范围
        val text = text.toString()
        if (TextUtils.isEmpty(text)) return
        // 绘制不变色的部分
        drawOriginText(canvas, text, middle)
        // 绘制变色的部分
        drawChangeText(canvas, text, middle)
    }

    /**
     * 2. 绘制变色的部分
     */
    private fun drawChangeText(canvas: Canvas, text: String, middle: Int) {
        // 判断当前的朝向
        if (mDirection == Direction.LEFT_TO_RIGHT) {
            drawText(text, canvas, mChangePaint, 0, middle)
        } else {
            drawText(text, canvas, mChangePaint, width - middle, width)
        }
    }

    /**
     * 2. 绘制不变色的部分
     */
    private fun drawOriginText(canvas: Canvas, text: String, middle: Int) {
        if (mDirection == Direction.LEFT_TO_RIGHT) {
            drawText(text, canvas, mOriginPaint, middle, width)
        } else {
            drawText(text, canvas, mOriginPaint, 0, width - middle)
        }
    }

    private fun drawText(text: String, canvas: Canvas, paint: Paint?, start: Int, end: Int) {
        // 保持画布状态
        canvas.save()
        // 只绘制截取部分
        canvas.clipRect(Rect(start, 0, end, height))
        // 获取字体的bounds
        val bounds = Rect()
        paint!!.getTextBounds(text, 0, text.length, bounds)
        // x  就是代表绘制的开始部分  不考虑左右padding不相等的情况下 = 控件宽度的一半 - 字体宽度的一半
        val x = (width - bounds.width()) / 2
        // y  代表的是基线 baseLine
        val dy = bounds.height() / 2 - bounds.bottom
        // 计算基线的位置
        val baseLine = (height + bounds.height()) / 2 - dy
        canvas.drawText(text, x.toFloat(), baseLine.toFloat(), paint)
        // 释放画布
        canvas.restore()
    }

    /**
     * 1. 设置当前的进度
     * @param currentProgress  当前进度
     */
    fun setCurrentProgress(currentProgress: Float) {
        mCurrentProgress = currentProgress
        // 重新绘制  会不断的调用onDraw方法
        invalidate()
    }

    /**
     * 2.设置不同的朝向
     * @param direction  当前朝向
     */
    fun setDirection(direction: Direction) {
        mDirection = direction
    }

    /**
     * 3.设置原始不变色的字体颜色
     */
    fun setOriginColor(color: Int) {
        mOriginPaint!!.color = color
    }

    /**
     * 3.设置变色的字体颜色
     */
    fun setChangeColor(color: Int) {
        mChangePaint!!.color = color
    }

    init {
        initPaint(context, attrs)
    }
}