import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SendIntent)
public class SendIntent: CAPPlugin {
    
    @objc func checkSendIntentReceived(_ call: CAPPluginCall) {
        let store = ShareStore.store
        if !store.processed {
            call.success([
                "text": store.text,
                "url": store.url,
                "image": store.image
            ])
            store.processed = true
        }
    }

    open func eval(js: String){
        self.bridge.eval(js: js);
    }
}
