package com.phonepe.payment.capacitor;

import static com.phonepe.payment.capacitor.DataUtil.convertResultToString;
import static com.phonepe.payment.capacitor.DataUtil.handleException;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.APP_ID;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.BODY;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.CHECKSUM;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.ENABLE_LOGS;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.ENVIRONMENT;
import static com.phonepe.payment.capacitor.GlobalConstants.Argument.MERCHANT_ID;
import static com.phonepe.payment.capacitor.GlobalConstants.PHONEPE_PAYMENT_SDK;
import static com.phonepe.payment.capacitor.GlobalConstants.RequestCode.B2B_PG;
import static com.phonepe.payment.capacitor.GlobalConstants.RequestCode.CONTAINER;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.APPLICATION_NAME;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.ERROR;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.FAILURE;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.PACKAGE_NAME;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.SUCCESS;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.VERSION;
import static com.phonepe.payment.capacitor.LogUtil.logInfo;
import static com.phonepe.payment.capacitor.GlobalConstants.Response.STATUS;
import static com.phonepe.payment.capacitor.LogUtil.enableLogs;


import android.app.Activity;
import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.phonepe.intent.sdk.api.B2BPGRequest;
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder;
import com.phonepe.intent.sdk.api.PhonePe;
import com.phonepe.intent.sdk.api.UPIApplicationInfo;
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment;
import com.phonepe.intent.sdk.api.models.SDKType;

import org.json.JSONArray;

import java.util.Objects;

@CapacitorPlugin(name = PHONEPE_PAYMENT_SDK, requestCodes = {B2B_PG, CONTAINER})
public class PhonePePaymentSDKPlugin extends Plugin {

    public PhonePePaymentSDKPlugin() {
        PhonePe.setAdditionalInfo(SDKType.IONIC);
    }

    @PluginMethod
    public void isPhonePeInstalled(PluginCall call) {
        logInfo("started isPhonePeInstalled");
        try {
            JSObject result = new JSObject();
            result.put(STATUS, PhonePe.isPhonePeAppInstalled(true));
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void isPaytmInstalled(PluginCall call) {
        logInfo("started isPaytmInstalled");
        try {
            JSObject result = new JSObject();
            result.put(STATUS, PhonePe.isPayTMAppInstalled(true));
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void isGpayInstalled(PluginCall call) {
        logInfo("started isGpayInstalled");
        try {
            JSObject result = new JSObject();
            result.put(STATUS, PhonePe.isGooglePayAppInstalled(true));
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void getPackageSignatureForAndroid(PluginCall call) {
        logInfo("started getPackageSignatureForAndroid");
        try {
            JSObject result = new JSObject();
            result.put(STATUS, PhonePe.getPackageSignature());
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void getUpiAppsForAndroid(PluginCall call) {
        logInfo("started getUpiAppsForAndroid");
        try {
            JSONArray jsonArray = new JSONArray();
            for (UPIApplicationInfo app : PhonePe.getUpiApps()) {
                JSObject appJson = new JSObject();
                appJson.put(PACKAGE_NAME, app.getPackageName());
                appJson.put(APPLICATION_NAME, app.getApplicationName());
                appJson.put(VERSION, String.valueOf(app.getVersion()));
                jsonArray.put(appJson);
            }
            JSObject result = new JSObject();
            result.put(STATUS, jsonArray.toString());
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void init(PluginCall call) {
        logInfo("started init");
        try {
            enableLogs = call.getData().getBoolean(ENABLE_LOGS);
            String environment = call.getData().getString(ENVIRONMENT);
            String merchantId = call.getData().getString(MERCHANT_ID);
            if (environment == null || environment.isEmpty()
                || merchantId == null || merchantId.isEmpty()
            )
                throw new IllegalArgumentException("Invalid environment or merchantId!");

            PhonePeEnvironment ppEnvironment;
            if (Objects.equals(environment, PhonePeEnvironment.SANDBOX.name()))
                ppEnvironment = PhonePeEnvironment.SANDBOX;
            else ppEnvironment = PhonePeEnvironment.RELEASE;

            JSObject result = new JSObject();
            result.put(STATUS, PhonePe.init(getContext(),
                ppEnvironment,
                merchantId,
                call.getData().getString(APP_ID)
            ));
            call.resolve(result);
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }

    @PluginMethod
    public void startTransaction(PluginCall call) {
        try {
            logInfo("startTransaction initialized");
            saveCall(call);
            String checksum = call.getData().getString(CHECKSUM);
            String requestBody = call.getData().getString(BODY);
            if (checksum == null || checksum.isEmpty()
                || requestBody == null || requestBody.isEmpty()
            )
                throw new IllegalArgumentException("Invalid checksum or body!");

            B2BPGRequestBuilder b2BPGRequestBuilder = new B2BPGRequestBuilder();
            B2BPGRequest b2BPGRequest = b2BPGRequestBuilder.setChecksum(checksum)
                .setData(requestBody)
                .build();

            startActivityForResult(
                call,
                PhonePe.getImplicitIntent(
                    getContext(),
                    b2BPGRequest,
                    call.getData().getString(PACKAGE_NAME)
                ),
                B2B_PG
            );
        } catch (Exception ex) {
            handleException(ex, call);
        }
    }


    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);
        try {
            String intentData = convertResultToString(data);
            logInfo("handleOnActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + "data:" + intentData);
            if (requestCode == B2B_PG || requestCode == CONTAINER && getSavedCall() != null) {
                JSObject result = new JSObject();
                if (resultCode != Activity.RESULT_CANCELED) {
                    result.put(STATUS, SUCCESS);
                } else {
                    result.put(STATUS, FAILURE);
                    result.put(ERROR, intentData);
                }
                getSavedCall().resolve(result);
            }
        } catch (Exception ex) {
            logInfo("Exception in handleOnActivityResult:" + ex.getLocalizedMessage());
        }
    }
}
