package lex.utils.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.util.*

object CountryCodeUtil {

    private const val DEFAULT_COUNTRY = "US"

    var countryCode: String = DEFAULT_COUNTRY
        private set(value) {
            if (value.isNotBlank()) {
                field = value
            }
        }

    /**
     * Init country code
     *
     * Application中初始化
     * app内使用缓存
     * @param context
     */
    fun initCountryCode(context: Context) {
        countryCode = getDeviceCountryCode(context) ?: DEFAULT_COUNTRY
    }

    /**
     * Get device country code
     * 先取手机语言设置中的国家
     * 再取网络
     * @param context
     * @return
     */
    private fun getDeviceCountryCode(context: Context): String? {
        val localCountryCode = getCountryCodeFromLocal(getLocale(context))
        if (!localCountryCode.isNullOrBlank()) {
            return localCountryCode
        }

        // if Locale class country not available (tablets maybe), get country code from network
        return getCountryIso(context)?.parseCountryCode()
    }

    private fun getCountryCodeFromLocal(locale: Locale): String? {
        return locale.country.parseCountryCode()
    }

    private fun getLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales.get(0)
        } else {
            context.resources.configuration.locale
        }
    }

    private fun getCountryIso(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val simCountryIso = tm.simCountryIso.parseCountryCode()
        if (!simCountryIso.isNullOrBlank()) {
            return simCountryIso
        }

        return if (tm.phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            getCDMACountryIso()
        } else {
            tm.networkCountryIso
        }?.parseCountryCode()
    }

    private fun getCDMACountryIso(): String? {
        try {
            // try to get country code from SystemProperties private class
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)

            // get homeOperator that contain MCC + MNC
            val homeOperator = get.invoke(
                systemProperties,
                "ro.cdma.home.operator.numeric"
            ) as String

            // first 3 chars (MCC) from homeOperator represents the country code

            // mapping just countries that actually use CDMA networks

            if (homeOperator.length >= 3)
                when (Integer.parseInt(homeOperator.substring(0, 3))) {
                    330 -> return "PR"
                    310 -> return "US"
                    311 -> return "US"
                    312 -> return "US"
                    316 -> return "US"
                    283 -> return "AM"
                    460 -> return "CN"
                    455 -> return "MO"
                    414 -> return "MM"
                    619 -> return "SL"
                    450 -> return "KR"
                    634 -> return "SD"
                    434 -> return "UZ"
                    232 -> return "AT"
                    204 -> return "NL"
                    262 -> return "DE"
                    247 -> return "LV"
                    255 -> return "UA"
                }
        } catch (ignored: Throwable) {
        }

        return null
    }

    private fun String.parseCountryCode(): String? {
        return if (length == 2) {
            this.uppercase(Locale.getDefault())
        } else {
            null
        }
    }


    /**
     * Get language country
     *
     * @param context
     * @return en-US
     */
    fun getLanguageCountry(context: Context): String {
        val locale = getLocale(context)
        val localeCountryCode = getCountryCodeFromLocal(locale)
        val countryCode = if (!localeCountryCode.isNullOrBlank()) {
            localeCountryCode
        } else {
            // if Locale class country not available (tablets maybe), get country code from network
            getCountryIso(context)?.parseCountryCode()
        }

        var language = locale.language
        // rewrite depreciated two-letter codes
        if (language == "iw") language = "he" // Hebrew
        if (language == "in") language = "id" // Indonesian
        if (language == "ji") language = "yi" // Yiddish

        return try {
            String.format(Locale.US, "%s-%s", language, countryCode)
        } catch (e: Throwable) {
            "$language-$countryCode"
        }
    }

    /**
     * 无SIM卡时可能不准确
     *
     * @param context
     * @return
     */
    fun getTelephonyCountryCode(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkCountryIso
    }
}