package lex.utils.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.TimeZone;



public class DeviceUtil {

    private static String sVersionName;

    public DeviceUtil() {

    }

    /**
     * @return HUAWEI_TAS-AN00
     */
    public static String getDisplayName() {
        return getManufacturer() + "_" + getModel();
    }

    /**
     * 获取手机型号
     *
     * @return String
     * @author Henry
     * @date 2015-3-30
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取android 版本
     *
     * @return String
     * @author Henry
     * @date 2015-3-30
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getSerial() {
        return Build.SERIAL;
    }

    /**
     * 获取当前设备语言
     *
     * @param context {@link Context}
     * @return String
     * @author Henry
     * @date 2015-3-30
     */
    public static String getLanguage(Context context) {
        if (context == null) {
            return null;
        }
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    /**
     * 获取SDK版本号
     *
     * @return String
     * @author Henry
     * @date 2015-3-30
     */
    public static int getSDKVersion() {
        return Build.VERSION.SDK_INT;
    }


    public static String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    /**
     * @return 国家
     */
    public static String getCountry(Context context) {
        return CountryCodeUtil.INSTANCE.getCountryCode();
    }

    public static String getAppVersion(Context context) {
        try {
            if (sVersionName == null) {
                synchronized (DeviceUtil.class) {
                    if (sVersionName == null) {
                        sVersionName =
                                context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                    }
                }
                sVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            }
            return sVersionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCarrier(Context context) {
        TelephonyManager telephonyManager
                = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null == telephonyManager) {
            return "unknown";
        } else {
            return telephonyManager.getNetworkOperatorName();
        }
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 是否在充电
     */
    public static boolean isCharging(Context context) {
        boolean isCharging = false;
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        // 是否在充电
        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL;
        }
        return isCharging;
    }

    /**
     * 获取唯一id
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceID(Context context) {
        String deviceId = "";
        //如果sdk版本大于等于29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }
}
