package lex.utils.widget

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import java.util.*

/**
 * @Author dxl
 * @Date 2023/7/10 16:08
 * @Email lex911118@gmail.com
 * @Description This is EmailAutoCompleteView
 */
class EmailAutoCompleteView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatAutoCompleteTextView(context, attrs) {

    private val emailSufixs = arrayOf(
        "@163.us",
        "@qq.com",
        "@126.com",
        "@foxmail.com",
        "@gmail.com",
        "@yahoo.com",
        "@hotmail.com",
        "@msn.com",
        "@sohu.com",
        "@sina.com",
        "@yeah.net",
        "@outlook.net",
        "@outlook.com"
    )

    private val adapter by lazy {
        EmailAutoCompleteAdapter(emailSufixs)
    }

    fun newData(list: List<String>){
        adapter.newData(list)
    }

    fun addData(list: List<String>) {
        adapter.addData(list)
    }

    private class EmailAutoCompleteAdapter(emailSufixs: Array<String>) :
        BaseAdapter(), Filterable {
        private val originItems: MutableList<String> = mutableListOf(*emailSufixs)
        private val filteredItems = LinkedList<String>()

        fun newData(list: List<String>){
            originItems.clear()
            originItems.addAll(list)
        }

        fun addData(list: List<String>) {
            originItems.addAll(list)
        }

        override fun getCount(): Int {
            return filteredItems.size
        }

        override fun getItem(position: Int): String {
            return filteredItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val context = parent.context

            val textView = (convertView ?: TextView(context).apply {
                setTextColor(Color.BLACK)
                textSize = 20f
                layoutParams =
                    ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }) as TextView


            val count = count
            if (position != count - 1) {
                textView.setPadding(38, 38, 38, 0)
            } else {
                textView.setPadding(38, 38, 38, 38)
            }
            textView.text = getItem(position)
            return textView
        }

        override fun getFilter(): Filter {
            return TextFilter()
        }

        private inner class TextFilter : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                var constraint: CharSequence? = constraint
                if (constraint == null) {
                    constraint = ""
                }
                val key = constraint.toString().lowercase(Locale.getDefault())
                filteredItems.clear()
                for (originItem in originItems) {
                    val lowerCase = originItem.lowercase(Locale.getDefault())
                    if (lowerCase.contains(key) && lowerCase.lastIndexOf("@") > 0) {
                        filteredItems.add(originItem)
                    }
                }
                val i = key.lastIndexOf("@")
                val text: String
                text = if (i > 0) {
                    key.substring(0, i)
                } else {
                    key
                }
                for (originItem in originItems) {
                    if (filteredItems.contains(originItem)) continue
                    val s = text + originItem
                    if (s.lowercase(Locale.getDefault())
                            .startsWith(key) && !filteredItems.contains(s)
                    ) {
                        filteredItems.add(s)
                    }
                }
                //                results.count = temp.size();
//                results.values = temp;
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                notifyDataSetChanged()
            }
        }
    }

}