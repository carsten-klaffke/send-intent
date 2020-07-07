import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitor.ionicframework.com/docs/plugins/ios
 */
@objc(SendIntent)
public class SendIntent: CAPPlugin {
    
    let store = ShareStore.store

    @objc func checkSendIntentReceived(_ call: CAPPluginCall) {
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

    open func writeToStore(text: String, url: String, image: String){
        store.text = text
        store.url = url
        store.image = image
    }
}
