package com.wk.vpindicator.adapter

import android.view.View
import android.view.ViewGroup

interface IndicatorBaseAdapter {

    fun getCount(): Int

    fun getView(parent: ViewGroup, position: Int): View

    fun highlightIndicator(view: View, highlightColor: Int)

    fun restoreIndicator(view: View, originColor: Int)
}