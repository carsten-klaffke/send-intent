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
            let firstItem: JSObject? = store.shareItems.first
            let additionalItems: Array<JSObject> = store.shareItems.count > 1 ? Array(store.shareItems[..<1]) : []
            
            call.resolve([
                "title": firstItem?["title"] ?? "",
                "description": firstItem?["description"] ?? "",
                "type": firstItem?["type"] ?? "",
                "url": firstItem?["url"] ?? "",
                "additionalItems": additionalItems
            ])
            store.processed = true
        } else {
            call.reject("No processing needed.")
        }
    }

    public override func load() {
        let nc = NotificationCenter.default
        nc.addObserver(self, selector: #selector(eval), name: Notification.Name("triggerSendIntent"), object: nil)
    }

    @objc open func eval(){
        self.bridge?.eval(js: "window.dispatchEvent(new Event('sendIntentReceived'))");
    }

}
