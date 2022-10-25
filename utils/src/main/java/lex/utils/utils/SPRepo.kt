package lex.utils.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import lex.utils.app.APP
import lex.utils.exts.isNotEmpty

class SPRepo(fileName: String) {

    private val sp: SharedPreferences by lazy {
        APP.context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun getString(key: String): String {
        return getString(key, "") ?: ""
    }

    fun getString(key: String, defValue: String?): String? {
        return try {
            sp.getString(key, defValue)
        } catch (e: ClassCastException) {
            defValue
        }
    }

    fun getInt(key: String): Int {
        return getInt(key, 0)
    }

    fun getInt(key: String, defValue: Int): Int {
        return try {
            sp.getInt(key, defValue)
        } catch (e: ClassCastException) {
            defValue
        }
    }

    fun getLong(key: String): Long {
        return getLong(key, 0L)
    }

    fun getLong(key: String, defValue: Long): Long {
        return try {
            sp.getLong(key, defValue)
        } catch (e: ClassCastException) {
            defValue
        }
    }

    fun getBoolean(key: String): Boolean {
        return getBoolean(key, false)
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return try {
            sp.getBoolean(key, defValue)
        } catch (e: ClassCastException) {
            defValue
        }
    }

    fun getFloat(key: String): Float {
        return getFloat(key, 0f)
    }

    fun getFloat(key: String, defValue: Float): Float {
        return try {
            sp.getFloat(key, defValue)
        } catch (e: ClassCastException) {
            defValue
        }
    }

    inline fun <reified T> getObject(key: String): T? {
        try {
            val json = getString(key, "")
            if (json.isNotEmpty) {
                return Gson().fromJson(json, T::class.java)
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> getList(key: String): List<T>? {
        try {
            val json = getString(key, "")
            if (json.isNotEmpty) {
                return Gson().fromJson<List<T>>(json, object : TypeToken<List<T>>() {}.type)
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Get map
     * 注意:json中的整数类型会被转换成浮点类型.
     * 如果想要整数类形 需要额外处理.
     * @param key
     * @return
     */
    fun getMap(key: String): Map<String, Any>? {
        try {
            val json = getString(key, "")
            if (json.isNotEmpty) {
                return Gson().fromJson<Map<String, Any>>(
                    json,
                    object : TypeToken<Map<String, Any>>() {}.type
                )
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    fun getStringMap(key: String): Map<String, String>? {
        try {
            val json = getString(key, "")
            if (json.isNotEmpty) {
                return Gson().fromJson<Map<String, String>>(
                    json,
                    object : TypeToken<Map<String, String>>() {}.type
                )
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
        }
        return null
    }

    fun putString(key: String, value: String?) {
        sp.edit().putString(key, value).apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    fun putLong(key: String, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    fun putFloat(key: String, value: Float) {
        sp.edit().putFloat(key, value).apply()
    }

    fun <T> putObject(key: String, value: T?) {
        val json = if (value != null) {
            Gson().toJson(value)
        } else {
            ""
        }
        putString(key, json)
    }

    fun <T> putList(key: String, value: List<T>?) {
        val json = if (value.isNullOrEmpty()) {
            ""
        } else {
            Gson().toJson(value)
        }
        putString(key, json)
    }

    fun putMap(key: String, value: Map<String, Any>?) {
        val json = if (value.isNullOrEmpty()) {
            ""
        } else {
            Gson().toJson(value)
        }
        putString(key, json)
    }

    fun putStringMap(key: String, value: Map<String, String>?) {
        val json = if (value.isNullOrEmpty()) {
            ""
        } else {
            Gson().toJson(value)
        }
        putString(key, json)
    }

    fun removeKey(key: String) {
        sp.edit().remove(key).apply()
    }

    fun containsKey(key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * 清除所有已记录的值
     */
    fun cleanAll() {
        sp.edit().clear().apply()
    }
}