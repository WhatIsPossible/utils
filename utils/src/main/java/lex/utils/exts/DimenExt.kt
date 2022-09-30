package lex.utils.exts

import android.util.TypedValue
import lex.utils.app.APP

val Float.sp2px: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        APP.context.resources.displayMetrics
    ).toInt()