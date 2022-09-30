package lex.utils.exts

import android.util.DisplayMetrics
import android.util.TypedValue
import lex.utils.app.APP

val Float.sp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        displayMetrics
    ).toInt()

val Float.dp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        displayMetrics
    ).toInt()

private val displayMetrics: DisplayMetrics
    get() = APP.context.resources.displayMetrics