package com.wiser.scrollmultitermview

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * @author Wiser
 *
 * 滚动的多条item
 */
class ScrollMultitermView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    /**
     * 是否正在运行滚动
     */
    private var isRunningScroll = false

    /**
     * 是否能够运行滚动
     */
    private var isCanRunScroll = false

    /**
     * 是否第一次开始滚动
     */
    private var isFirstScroll = true

    private var scrollPopTask: ScrollPopTask? = null

    /**
     * 滚动时间间隔
     */
    private var scrollIntervalDuration: Int = 1500

    /**
     * 初始时延迟滚动时间间隔
     */
    private var initDelayDuration: Int = 100

    /**
     * 是否有顶部阴影边界
     */
    private var isTopFadingEdgeEnabled = true

    /**
     * 是否有底部阴影边界
     */
    private var isBottomFadingEdgeEnabled = false

    /**
     * 阴影边距
     */
    private var fadingEdgeLength = 40f

    /**
     * 首次滚动到的位置
     */
    private var firstTimeScrollToPosition = 2

    /**
     * 事件code
     */
    private var touchCode = TOUCH_EVENT_DEFAULT_CODE

    /**
     * 当前屏幕可见的最后一条item 坐标位置
     */
    private var lastVisibilityPosition = 0

    private val animatorSet = AnimatorSet()

    private var isCancelAnimator = true

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ScrollMultitermView)
        isTopFadingEdgeEnabled = ta.getBoolean(
            R.styleable.ScrollMultitermView_smv_isTopFadingEdgeEnabled,
            isTopFadingEdgeEnabled
        )
        isBottomFadingEdgeEnabled = ta.getBoolean(
            R.styleable.ScrollMultitermView_smv_isBottomFadingEdgeEnabled,
            isBottomFadingEdgeEnabled
        )
        scrollIntervalDuration = ta.getInt(
            R.styleable.ScrollMultitermView_smv_scrollIntervalDuration,
            scrollIntervalDuration
        )
        initDelayDuration = ta.getInt(
            R.styleable.ScrollMultitermView_smv_initDelayDuration,
            initDelayDuration
        )
        firstTimeScrollToPosition = ta.getInt(
            R.styleable.ScrollMultitermView_smv_firstTimeScrollToPosition,
            firstTimeScrollToPosition
        )
        fadingEdgeLength =
            ta.getInt(
                R.styleable.ScrollMultitermView_smv_fadingEdgeLength,
                fadingEdgeLength.toInt()
            ).toFloat()
        ta.recycle()

        scrollPopTask = ScrollPopTask(this)

        if (isTopFadingEdgeEnabled) {
            isVerticalFadingEdgeEnabled = true
            setFadingEdgeLength(dip2px(fadingEdgeLength))
        }

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE || newState == SCROLL_STATE_DRAGGING) {
                    val layoutManager: LayoutManager? = layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        lastVisibilityPosition = layoutManager.findLastVisibleItemPosition()
                    }
                    if (!isRunningScroll && touchCode == TOUCH_EVENT_UP_CODE) {
                        touchCode = TOUCH_EVENT_DEFAULT_CODE
                        resume()
                        scrollPopTask?.setPosition(if (lastVisibilityPosition >= 0) lastVisibilityPosition else 0)
                    }
                } else {
                    if (isRunningScroll && touchCode == TOUCH_EVENT_UP_CODE) {
                        pause()
                    }
                }
            }
        })
    }

    companion object {

        /**
         * 默认事件code
         */
        const val TOUCH_EVENT_DEFAULT_CODE = -1

        /**
         * 按下事件code
         */
        const val TOUCH_EVENT_DOWN_CODE = 1

        /**
         * 抬起事件code
         */
        const val TOUCH_EVENT_UP_CODE = 2

        /**
         * 滚动池任务
         */
        class ScrollPopTask(view: ScrollMultitermView) : Runnable {
            private var position: Int = 0
            private var reference: WeakReference<ScrollMultitermView>? = null

            init {
                this.reference = WeakReference(view)
                position = view.firstTimeScrollToPosition
            }

            fun setPosition(position: Int) {
                this.position = position
            }

            fun detach() {
                position = 0
                reference?.clear()
                reference = null
            }

            override fun run() {
                reference?.get()?.let {
                    if (it.isCanRunScroll) {
                        if (it.isRunningScroll) {
                            it.smoothScrollToPosition(position)
                            position++
                        }
                        if (it.isFirstScroll) {
                            it.isFirstScroll = false
                            it.postDelayed(it.scrollPopTask, it.initDelayDuration.toLong())
                        } else {
                            it.postDelayed(it.scrollPopTask, it.scrollIntervalDuration.toLong())
                        }
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchCode = TOUCH_EVENT_DOWN_CODE
                if (isRunningScroll) {
                    pause()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchCode = TOUCH_EVENT_UP_CODE
                if (!isRunningScroll) {
                    resume()
                    scrollPopTask?.setPosition(if (lastVisibilityPosition >= 0) lastVisibilityPosition else 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 开始滚动
     */
    fun start() {
        if (isRunningScroll) {
            stop()
        }
        isFirstScroll = true
        isRunningScroll = true
        isCanRunScroll = true
        post(scrollPopTask)
    }

    /**
     * 停止滚动
     */
    fun stop() {
        lastVisibilityPosition = 0
        scrollPopTask?.setPosition(0)
        isFirstScroll = false
        isRunningScroll = false
        isCanRunScroll = false
        removeCallbacks(scrollPopTask)
    }

    /**
     * 暂停滚动
     */
    fun pause() {
        isRunningScroll = false
    }

    /**
     * 恢复滚动
     */
    fun resume() {
        isRunningScroll = true
    }

    /**
     * 销毁
     */
    fun detach() {
        scrollPopTask?.detach()
        removeCallbacks(scrollPopTask)
        scrollPopTask = null
        animatorSet.cancel()
    }

    override fun getBottomFadingEdgeStrength(): Float {
        return if (isBottomFadingEdgeEnabled) {
            super.getBottomFadingEdgeStrength()
        } else 0f
    }

    /**
     * dip转换成px
     *
     * @param dpValue
     * @return
     */
    private fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}