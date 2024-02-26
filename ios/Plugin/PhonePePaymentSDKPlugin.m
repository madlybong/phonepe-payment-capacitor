#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(PhonePePaymentSDK, "PhonePePaymentSDK",
           CAP_PLUGIN_METHOD(init, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startContainerTransaction, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startTransaction, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isPhonePeInstalled, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isGpayInstalled, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isPaytmInstalled, CAPPluginReturnPromise);
)
