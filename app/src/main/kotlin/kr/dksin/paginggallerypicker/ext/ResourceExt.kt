package kr.dksin.paginggallerypicker.ext

import android.content.res.Resources
import android.util.TypedValue

fun Resources.dipToPx(dp: Int): Int {
    val scale = displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Resources.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), displayMetrics).toInt()
}

fun Resources.spToPx(sp: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), displayMetrics)
}