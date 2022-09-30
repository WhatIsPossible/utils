package lex.utils.exts

import android.util.DisplayMetrics
import android.util.TypedValue
import lex.utils.app.APP
import kotlin.math.roundToInt

val Float.sp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        displayMetrics
    ).roundToInt()

val Float.dp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        displayMetrics
    ).roundToInt()

fun getScreenWidth(): Int {
    return displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return displayMetrics.heightPixels
}

private val displayMetrics: DisplayMetrics
    get() = APP.context.resources.displayMetrics