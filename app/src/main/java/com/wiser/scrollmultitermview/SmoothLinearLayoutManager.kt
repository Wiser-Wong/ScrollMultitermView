package com.wiser.scrollmultitermview

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * @author wangxy
 *
 *         控制smoothScrollToPosition 滚动时间
 */
@SuppressLint("WrongConstant")
class SmoothLinearLayoutManager(
    context: Context, @RecyclerView.Orientation direction: Int = VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, direction, reverseLayout) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView!!.context) {
                // 返回：滑过1px时经历的时间(ms)。
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 480f / displayMetrics.densityDpi
                }
            }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

}