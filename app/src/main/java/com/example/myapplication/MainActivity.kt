package com.example.myapplication

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.wk.vpindicator.adapter.DefaultIndicatorAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private val data = arrayListOf("One", "Two", "Three", "Four", "Five", "Six", "Seven", "讲课费", "加快分解")

    override fun initData() {
        vp.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }

            override fun getCount(): Int {
                return data.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val textView = TextView(this@MainActivity)
                textView.text = data[position]
                textView.gravity = Gravity.CENTER
                container.addView(textView)
                return textView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

            }
        }
        tiv.setAdapter(DefaultIndicatorAdapter(data), vp)
    }

}
