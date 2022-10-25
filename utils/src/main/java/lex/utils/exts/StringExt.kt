package lex.utils.exts

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.util.Log

val String.i: Int
    get() = Log.i("测试", "${this}    ${Thread.currentThread().name}")

fun SpannableStringBuilder.appendText(
    text: CharSequence, vararg style: CharacterStyle
): SpannableStringBuilder {
    val start = length
    append(text)
    val end = length
    style.forEach {
        setSpan(it, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return this
}

inline val String?.isNotEmpty: Boolean
    get() = this != null && this.isNotEmpty()