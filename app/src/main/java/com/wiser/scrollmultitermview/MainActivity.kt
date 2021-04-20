package com.wiser.scrollmultitermview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = getData()
        smv?.layoutManager = SmoothLinearLayoutManager(this)
        smv?.adapter = MainAdapter(list)
        smv?.smoothScrollToPosition(if (list.size > 2) 2 else if (list.size > 0) list.size - 1 else 0)
        smv?.start()
    }

    private fun getData(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()
        for (i in (0..200)) {
            if (i % 2 == 0) {
                list.add("我是一个粉刷匠$i")
            } else {
                list.add("梦为努力浇了水，爱在背后往前推，作曲：林俊杰$i")
            }
        }
        return list
    }

    override fun onDestroy() {
        super.onDestroy()
        smv?.detach()
    }
}