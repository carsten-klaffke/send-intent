import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SendIntentPlugin)
public class SendIntentPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SendIntentPlugin"
    public let jsName = "SendIntent"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "checkSendIntentReceived", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = SendIntent()

    let store = ShareStore.store

    @objc func checkSendIntentReceived(_ call: CAPPluginCall) {
        if !store.processed {
            let firstItem: JSObject? = store.shareItems.first
            let additionalItems: Array<JSObject> = store.shareItems.count > 1 ? Array(store.shareItems[1...]) : []

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

    @objc func finish(_ call: CAPPluginCall) {
        call.resolve();
    }

    public override func load() {
        let nc = NotificationCenter.default
        nc.addObserver(self, selector: #selector(eval), name: Notification.Name("triggerSendIntent"), object: nil)
    }

    @objc open func eval(){
        self.bridge?.eval(js: "window.dispatchEvent(new Event('sendIntentReceived'))");
    }
}
