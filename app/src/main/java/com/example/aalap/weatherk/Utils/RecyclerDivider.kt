package com.example.aalap.weatherk.Utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.aalap.weatherk.R

class RecyclerDivider
/**
 * Default divider will be used
 */
(context: Context) : RecyclerView.ItemDecoration() {

    val TAG = "Divider:"

    private val ATTRS = intArrayOf(android.R.attr.listDivider)

    private val divider: Drawable?

    init {
        val styledAttributes = context.obtainStyledAttributes(ATTRS)
        divider = ContextCompat.getDrawable(context, R.drawable.recycler_divider)
        styledAttributes.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + divider!!.intrinsicHeight
            Log.d(TAG, "onDraw: $left:$top:$right:$bottom")

            val gap_margin = (right * .03).toInt()

            divider.setBounds(left + gap_margin, top, right - gap_margin, bottom)
            divider.draw(c)
        }
    }
}

