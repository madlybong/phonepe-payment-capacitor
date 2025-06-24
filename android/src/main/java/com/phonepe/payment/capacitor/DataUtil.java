package com.phonepe.payment.capacitor;

import static com.phonepe.payment.capacitor.GlobalConstants.Response.INITIALIZE_PHONEPE_SDK;
import static com.phonepe.payment.capacitor.LogUtil.logInfo;


import com.getcapacitor.PluginCall;
import com.phonepe.intent.sdk.api.PhonePeInitException;


import java.util.Objects;


public class DataUtil {

    public static void handleException(Exception ex, PluginCall call) {
        LogUtil.logInfo("handleException: ${this.localizedMessage}");
        if (ex instanceof PhonePeInitException)
            call.reject(INITIALIZE_PHONEPE_SDK, new Exception(INITIALIZE_PHONEPE_SDK));
        else
            call.reject(ex.getLocalizedMessage(), ex);
    }

    public static String convertResultToString(android.content.Intent intent) {
        try {
            StringBuilder result = new StringBuilder();
            if (intent != null && intent.getExtras() != null && intent.getExtras().keySet().size() > 0)
                for (String key : intent.getExtras().keySet())
                    if (intent.getExtras().get(key) != null)
                        result.append(key).append(":").append(Objects.requireNonNull(intent.getExtras().get(key)));
            return result.toString();
        } catch (Exception ex) {
            logInfo("Exception in convertResultToString:" + ex.getLocalizedMessage());
            return "";
        }
    }
}
