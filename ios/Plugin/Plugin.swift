import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SendIntent)
public class SendIntent: CAPPlugin {
    
    @objc func checkSendIntentReceived(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.success([
            "value": value
        ])
    }
    
    static func eval(js: String){
        self.bridge.eval(js);
    }
}
