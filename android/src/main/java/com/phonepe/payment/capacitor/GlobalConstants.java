package com.phonepe.payment.capacitor;

public class GlobalConstants {

    public static final String PHONEPE_PAYMENT_SDK = "PhonePePaymentSDK";
    
    public static class Argument{
        public static final String ENVIRONMENT = "environment";
        public static final String MERCHANT_ID = "merchantId";
        public static final String APP_ID = "appId";
        public static final String BODY = "body";
        public static final String CHECKSUM = "checksum";
        public static final String ENABLE_LOGS = "enableLogging";
    }

    public static class RequestCode {
        public static final int B2B_PG = 725;
        public static final int  CONTAINER = 101;
    }

    public static class Response {
        public static final String STATUS = "status";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILURE = "FAILURE";
        public static final String ERROR = "error";
        public static final String PACKAGE_NAME = "packageName";
        public static final String APPLICATION_NAME = "applicationName";
        public static final String VERSION = "version";
        public static final String INITIALIZE_PHONEPE_SDK = "Please, Initialize PhonePe SDK!";
    }
}
