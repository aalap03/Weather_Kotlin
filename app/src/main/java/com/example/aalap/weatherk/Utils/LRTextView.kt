package com.example.aalap.weatherk.Utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class LRTextView : android.support.v7.widget.AppCompatTextView {


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    internal fun init(context: Context) {
        typeface = Typeface.createFromAsset(context.assets, "fonts/Lato-Regular.ttf")
    }

}
