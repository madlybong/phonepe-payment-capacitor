package com.phonepe.payment.capacitor;

import android.util.Log;

public class LogUtil {

    public static boolean enableLogs = false;

    public static void logInfo( String message) {
        if (enableLogs)
            Log.i(GlobalConstants.PHONEPE_PAYMENT_SDK, message);
    }
}
