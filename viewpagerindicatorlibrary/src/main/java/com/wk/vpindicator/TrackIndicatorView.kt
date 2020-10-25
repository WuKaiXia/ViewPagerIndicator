package com.wk.vpindicator

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import com.wk.vpindicator.adapter.IndicatorBaseAdapter


class TrackIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : HorizontalScrollView(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private val tag = "TrackIndicatorView"

    // 自定义适配器
    private var mAdapter: IndicatorBaseAdapter? = null

    // Item的容器因为ScrollView只允许加入一个孩子
    private var mIndicatorContainer: LinearLayout = LinearLayout(context)

    private var mViewPager: ViewPager? = null
    private var mCurrentPosition = 0

    // 获取一屏显示多少个Item,默认是0
    private var mTabVisibleNumbers = 0

    private var mTextSelectColor = Color.RED
    private var mTextUnSelectColor = Color.BLACK

    // 每个Item的宽度
    private var mItemWidth = 0

    private var isClickScroll = false

    init {
        addView(mIndicatorContainer)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrackIndicatorView)
        mTabVisibleNumbers = typedArray.getInt(R.styleable.TrackIndicatorView_tabVisibleNumbers, mTabVisibleNumbers)
        mTextSelectColor = typedArray.getColor(R.styleable.TrackIndicatorView_indicator_text_select_color, mTextSelectColor)
        mTextUnSelectColor = typedArray.getColor(R.styleable.TrackIndicatorView_indicator_text_un_select_color, mTextUnSelectColor)
        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (changed) {
            mItemWidth = getItemWidth()
            mIndicatorContainer.forEach { view ->
                view.layoutParams.width = mItemWidth
            }
        }
    }

    /**
     * 获取item 宽度
     */
    private fun getItemWidth(): Int {
        var itemWidth: Int
        if (mTabVisibleNumbers != 0) {
            itemWidth = width / mTabVisibleNumbers
            return itemWidth
        }
        // 如果没有指定获取最宽的一个作为ItemWidth
        var maxItemWidth = 0

        // 总的宽度
        var allWidth = 0
        mIndicatorContainer.forEach { itemView ->
            val childWidth = itemView.measuredWidth
            maxItemWidth = maxItemWidth.coerceAtLeast(childWidth)
            allWidth += childWidth
        }
        itemWidth = maxItemWidth

        // 如果不足一个屏那么宽度就为  width/mItemCounts
        if (allWidth < width) {
            itemWidth = width / mIndicatorContainer.childCount
        }
        return itemWidth
    }

    fun setCurrentPosition(position: Int) {
        mViewPager?.setCurrentItem(position, false)
    }

    fun setAdapter(adapter: IndicatorBaseAdapter?, viewPager: ViewPager? = null) {
        mViewPager = viewPager
        mViewPager?.addOnPageChangeListener(this)

        checkAdapter(adapter) {
            val count = it.getCount()
            for (i in 0 until count) {
                val view = it.getView(mIndicatorContainer, i)
                mIndicatorContainer.addView(view)
                if (view is ColorTrackTextView) {
                    view.setChangeColor(mTextSelectColor)
                    view.setOriginColor(mTextUnSelectColor)
                } else {
                    if (i == mCurrentPosition) {
                        it.highlightIndicator(view, mTextSelectColor)
                    } else {
                        it.restoreIndicator(view, mTextUnSelectColor)
                    }
                }
                switchIndicatorClick(view, i)
            }
        }
    }

    /**
     * indicator item的点击事件
     */
    private fun switchIndicatorClick(view: View, position: Int) {
        view.setOnClickListener {
            if (mCurrentPosition == position) return@setOnClickListener
            isClickScroll = true
            mViewPager?.setCurrentItem(position, false)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        // 在ViewPager滚动的时候会不断的调用该方法
        Log.e(tag, "position --> $position  positionOffset --> $positionOffset")
        // 在不断滚动的时候让头部的当前Item一直保持在最中心
        indicatorScrollTo(position, positionOffset, isClickScroll)
        setColorTrack(position, positionOffset)
        isClickScroll = false
    }

    /**
     * indicator跟随viewpager滚动
     */
    private fun indicatorScrollTo(position: Int, positionOffset: Float, isSmooth: Boolean) {
        // 当前偏移量
        val currentOffset = (position + positionOffset) * mItemWidth
        // 原始的向左偏移量
        val originLeftOffset = (width - mItemWidth) / 2
        // 当前应该滚动的位置
        val scrollOffset = currentOffset - originLeftOffset
        if (isSmooth) {
            smoothScrollTo(scrollOffset.toInt(), 0)
        } else {
            scrollTo(scrollOffset.toInt(), 0)
        }
    }

    override fun onPageSelected(position: Int) {
        if (position != mCurrentPosition && position < mIndicatorContainer.childCount) {
            val lastView = mIndicatorContainer[mCurrentPosition]
            mCurrentPosition = position
            val currentView = mIndicatorContainer[mCurrentPosition]
            checkAdapter {
                it.restoreIndicator(lastView, mTextUnSelectColor)
                it.highlightIndicator(currentView, mTextSelectColor)
            }
        }
    }

    private fun setColorTrack(position: Int, positionOffset: Float) {
        if (position < mIndicatorContainer.childCount - 1) {
            val leftView = mIndicatorContainer[position]
            val rightView = mIndicatorContainer[position + 1]
            if (leftView is ColorTrackTextView && rightView is ColorTrackTextView) {
                leftView.setDirection(ColorTrackTextView.Direction.RIGHT_TO_LEFT)
                leftView.setCurrentProgress(1 - positionOffset)
                rightView.setDirection(ColorTrackTextView.Direction.LEFT_TO_RIGHT)
                rightView.setCurrentProgress(positionOffset)
            }
        }
    }

    /**
     * 检查adapter是否为空
     */
    private inline fun checkAdapter(
        adapter: IndicatorBaseAdapter? = null,
        block: (IndicatorBaseAdapter) -> Unit
    ) {
        mAdapter?.let {
            block(it)
            return
        }
        if (adapter == null) {
            throw NullPointerException("Adapter can not be null!")
        }
        mAdapter = adapter
        block(mAdapter!!)
    }
}