package com.wk.vpindicator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wk.vpindicator.ColorTrackTextView
import com.wk.vpindicator.R
import kotlinx.android.synthetic.main.indicator_text.view.*

class DefaultIndicatorAdapter(private val data: List<String>) : IndicatorBaseAdapter {

    override fun getCount(): Int {
        return data.size
    }

    override fun getView(parent: ViewGroup, position: Int): View {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.indicator_text, parent, false)
        view?.tv?.text = data[position]
        return view
    }

    override fun highlightIndicator(view: View, highlightColor: Int) {
        if (view is ColorTrackTextView) {
            view.setCurrentProgress(1f)
        } else {
            view.tv?.setTextColor(highlightColor)
        }
    }

    override fun restoreIndicator(view: View, originColor: Int) {
        if (view is ColorTrackTextView) {
            view.setCurrentProgress(0f)
        } else {
            view.tv?.setTextColor(originColor)
        }
    }
}