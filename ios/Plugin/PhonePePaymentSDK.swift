import Foundation
import Capacitor
import PhonePePayment

@objc(PhonePePaymentSDK)
public class PhonePePaymentSDK: CAPPlugin {
    
    private var ppPayment: PPPayment?
    private var controller : UIViewController? {
        UIApplication.shared.windows.first?.rootViewController
    }
    private var call: CAPPluginCall?
    
    public override func load() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(handleOpenURL(_:)),
                                               name: Notification.Name.capacitorOpenURL,
                                               object: nil)
    }
    
    @objc func `init`(_ call: CAPPluginCall) {
        self.call = call
        let arguments = call.options
        guard let env = arguments?[Constants.environment] as? String, !env.isEmpty else {
            call.reject("environment should not be empty")
            return
        }
        
        guard let environment = IonicEnvironment(rawValue: env) else {
            call.reject( "environment value is not correct")
            return
        }
        
        let enableLogs = arguments?[Constants.enableLogs] as? Bool ?? false
        let appId = arguments?[Constants.appId] as? String
        
        ppPayment = PPPayment(environment: environment[], enableLogging: enableLogs, appId: appId)
        ppPayment?.setAdditionalInfo(sdkType: .ionic)
        call.resolve([Constants.status: true])
    }
    
    @objc func startTransaction(_ call: CAPPluginCall) {
        self.call = call
        DispatchQueue.main.async {
            guard let viewController = self.controller else {
                call.reject("Controller not present")
                return
            }
            
            guard let ppPayment = self.ppPayment else {
                call.reject("Initialize PhonePe SDK! first")
                return
            }
            
            self.makeTransactionRequest(call.options) { request in
                
                guard let request = request else {
                    return
                }
                
                ppPayment.startPG(transactionRequest: request,
                                  on: viewController,
                                  animated: true) { request, state in
                    self.handleResult(state: state, call: call)
                }
            }
        }
        
    }
    
    @objc func isPhonePeInstalled(_ call: CAPPluginCall) {
        call.resolve([Constants.status: PPPayment.isPhonePeInstalled()])
    }
    
    @objc func isGpayInstalled(_ call: CAPPluginCall) {
        call.resolve([Constants.status: PPPayment.isGPayAppPresent()])
    }
    
    @objc func isPaytmInstalled(_ call: CAPPluginCall) {
        call.resolve([Constants.status: PPPayment.isPaytmAppPresent()])
    }
    
    
    // MARK: Private Methods
    private func makeTransactionRequest(_ arguments: [AnyHashable: Any], completion:  @escaping ((PPTransactionRequest?) -> Void)) {
        guard let body = arguments[Constants.body] as? String, !body.isEmpty else {
            call?.reject("body should not be empty")
            return
        }
        
        guard let checksum = arguments[Constants.checksum] as? String, !checksum.isEmpty else {
            call?.reject("checksum should not be empty")
            return
        }
        
        let apiEndPoint = arguments[Constants.apiEndPoint] as? String ?? Constants.pgPay
        
        let headers = arguments[Constants.headers] as? [String: String] ?? [:]
        
        let appSchema = arguments[Constants.appSchema] as? String ?? ""
        
        let request = PPTransactionRequest(body: body,
                                           apiEndPoint: apiEndPoint,
                                           checksum: checksum,
                                           headers: headers,
                                           appSchema: appSchema)
        
        completion(request)
    }
    
    private func handleResult(state: PPResultState, call: CAPPluginCall) {
        var dict: [String: String] = [:]
        switch state {
        case .success:
            dict = [Constants.status: Constants.success]
        case .failure(let error):
            dict = [Constants.status: Constants.failure, Constants.error: error.localizedDescription]
        case .interrupted(let error):
            dict = [Constants.status: Constants.interrupted, Constants.error: error.localizedDescription]
        @unknown default:
            dict = [Constants.status: Constants.unknown]
        }
        
        print("RESULT:", separator: " ")
        
        call.resolve(dict)
    }
    
    @objc private func handleOpenURL(_ notification: Notification) {
        if let object = notification.object as? [AnyHashable: Any], let url = object[Constants.url] as? URL {
            let _ = PPPayment.checkDeeplink(url)
        }
    }
}


// MARK: Ionic Environment Enum
enum IonicEnvironment: String {
    case SANDBOX
    case PRODUCTION
    
    subscript() -> Environment {
        switch self {
        case .SANDBOX:
            return .sandbox
        case .PRODUCTION:
            return .production
        }
    }
}
