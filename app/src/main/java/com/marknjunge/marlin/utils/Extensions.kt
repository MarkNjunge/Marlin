package com.marknjunge.marlin.utils

import android.view.View
import android.view.ViewGroup

fun View.setMargins(left: Int = this.marginParams.leftMargin,
                    top: Int = this.marginParams.topMargin,
                    right: Int = this.marginParams.rightMargin,
                    bottom: Int = this.marginParams.bottomMargin
) {

    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        this.requestLayout()
    }
}

fun View.updatePadding(start: Int = this.paddingStart,
                       top: Int = this.paddingTop,
                       end: Int = this.paddingEnd,
                       bottom: Int = paddingBottom
) {
    this.setPadding(left, top, right, bottom)
}

val View.marginParams: ViewGroup.MarginLayoutParams
    get() = (this.layoutParams as ViewGroup.MarginLayoutParams)