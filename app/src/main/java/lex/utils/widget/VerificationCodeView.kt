package lex.utils.widget

import android.content.Context
import android.graphics.Color
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import lex.utils.R


/**
 * @Author dxl
 * @Date 2023/8/15 13:56
 * @Email lex911118@gmail.com
 * @Description This is VerificationCodeView
 */
class VerificationCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val mTextColor = Color.BLACK
    private val mWidth = context.resources.getDimensionPixelSize(R.dimen.dp_48)

    private val list: Array<AppCompatEditText>

    var code: String
        set(value) {
            value.forEachIndexed { index, c ->
                list.getOrNull(index)?.setText(c.toString())
            }
        }
        get() {
            val sp = StringBuffer()
            list.forEach {
                sp.append(it.text)
            }
            return sp.toString()
        }

    var contentChangeListener: ((code: String) -> Unit)? = null

    init {
        list = createEditTexts()
        list.forEachIndexed { index, editText ->
            addView(editText)
            editText.doAfterTextChanged {
                if (it?.isNotEmpty() == true) {
                    list.getOrNull(index + 1)?.run {
                        requestFocus()
                    }
                }
                contentChangeListener?.invoke(code)
            }

            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    list.getOrNull(index - 1)?.run {
                        requestFocus()
                    }
                    return@setOnKeyListener true; // Consume the event
                }
                return@setOnKeyListener false
            }


            editText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && editText.text?.isNotEmpty() == true) {
                    editText.selectAll()
                }
            }
        }
    }

    private fun createEditTexts(): Array<AppCompatEditText> {
        val list = ArrayList<AppCompatEditText>(6)

        for (i in 0 until 6) {
            list.add(AppCompatEditText(context).apply {
                textSize = 20f
                setTextColor(mTextColor)
                gravity = Gravity.CENTER
                imeOptions = EditorInfo.IME_ACTION_NEXT
                inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                filters = arrayOf(LengthFilter(1))
                setEms(1)
                setBackgroundResource(R.drawable.selector_edit_bg)
                id = generateViewId()
            })
        }
        list.forEachIndexed { index, appCompatEditText ->
            appCompatEditText.layoutParams = LayoutParams(mWidth, mWidth).apply {
                if (index == 0) {
                    horizontalChainStyle = LayoutParams.CHAIN_SPREAD_INSIDE
                    startToStart = LayoutParams.PARENT_ID
                } else {
                    startToEnd = list[index - 1].id
                }
                if (index == list.size - 1) {
                    endToEnd = LayoutParams.PARENT_ID
                } else {
                    endToStart = list[index + 1].id
                }
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            }

        }
        return list.toTypedArray()
    }

}