//
//  Constants.swift
//  Plugin
//
//  Created by Rajan Arora on 15/09/23.
//  Copyright © 2023 Max Lynch. All rights reserved.
//

import Foundation

enum SDKMethodKeys: String {
    case initSDK = "init"
    case isPhonePeInstalled
    case isPaytmAppInstalled
    case isGPayAppInstalled
    case startContainerTransaction
    case startTransaction
}

struct Constants {
    
    //KEYS
    static let environment = "environment"
    static let enableLogs = "enableLogs"
    static let appId = "appId"
    static let body = "body"
    static let apiEndPoint = "apiEndPoint"
    static let checksum = "checksum"
    static let headers = "headers"
    static let appSchema = "appSchema"
    static let status = "status"
    static let error = "error"
    static let url = "url"
    
    // Values
    static let success = "SUCCESS"
    static let failure = "FAILURE"
    static let interrupted = "INTERRUPTED"
    static let unknown = "UNKNOWN"
    
    // End Point
    static let pgPay = "/pg/v1/pay"
}
