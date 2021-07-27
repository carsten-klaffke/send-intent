# Send-Intent

This is a small Capacitor plugin meant to be used in Ionic applications for checking if your App was targeted as a share goal. It supports both Android and iOS. So far, it checks and returns "SEND"-intents of mimeType "text/plain", "image" or "application/octet-stream" (files).

Check out my app [mindlib - your personal mind library](https://play.google.com/store/apps/details?id=de.mindlib) to see it in action.

## Usage

Register in JS

```js
import "send-intent";
```

Sample call

```js
import {Plugins} from '@capacitor/core';
const {SendIntent} = Plugins;

Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
    if (result.text) {
        // ...
    }
});
```

## **Android**

Configure AndroidManifest.xml

```xml
<activity
    android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale"
    android:name="io.ionic.starter.MainActivity"
    android:label="@string/title_activity_main"
    android:theme="@style/AppTheme.NoActionBarLaunch"
    android:launchMode="singleTask">

    <intent-filter>
          <action android:name="android.intent.action.SEND" />

          <category android:name="android.intent.category.DEFAULT" />
          <category android:name="android.intent.category.BROWSABLE" />

          <data android:mimeType="text/plain" />
          <data android:mimeType="image/*" />
          <data android:mimeType="application/octet-stream" />
    </intent-filter>
</activity>
```

If you want to use checkIntent as a listener, you need to add the following code to your MainActivity:

```java
@Override
protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    String action = intent.getAction();
    String type = intent.getType();
    if (Intent.ACTION_SEND.equals(action) && type != null) {
        bridge.getActivity().setIntent(intent);
        bridge.eval("window.dispatchEvent(new Event('sendIntentReceived'))", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
            }
        });
    }
}
```

And then add the listener to your client:

```js
window.addEventListener("sendIntentReceived", () => {
   Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
        if (result.text) {
            // ...
        }
    });
})
```

Using SendIntent as a listener can be useful if the intent doesn't trigger a rerender of your app.

## **iOS**

Create a share extension ([Creating an App extension](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionCreation.html#//apple_ref/doc/uid/TP40014214-CH5-SW1))

Code for the ShareViewController:

```swift
import UIKit
import Social
import MobileCoreServices

class ShareViewController: SLComposeServiceViewController {

    private var urlString: String?
    private var textString: String?
    private var imageString: String?
    private var fileString: String?

    override func isContentValid() -> Bool {
        // Do validation of contentText and/or NSExtensionContext attachments here
        print(contentText ?? "content is empty")
        return true
    }

    override func didSelectPost() {
        var urlString = "YOUR_APP_URL_SCHEME://?text=" + (self.textString ?? "");
        urlString = urlString + "&url=" + (self.urlString ?? "");
        urlString = urlString + "&image=" + (self.imageString ?? "");
        urlString = urlString + "&file=" + (self.fileString?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? "");
        let url = URL(string: urlString)!
        openURL(url)
        self.extensionContext!.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // To add configuration options via table cells at the bottom of the sheet, return an array of SLComposeSheetConfigurationItem here.
        return []
    }

    override func viewDidLoad() {
      super.viewDidLoad()

      let extensionItem = extensionContext?.inputItems[0] as! NSExtensionItem
      let contentTypeURL = kUTTypeURL as String
      let contentTypeText = kUTTypeText as String

      for attachment in extensionItem.attachments as! [NSItemProvider] {

          attachment.loadItem(forTypeIdentifier: contentTypeURL, options: nil, completionHandler: { (results, error) in
                if results != nil {
                let url = results as! URL?
                if url!.isFileURL {
                    do {
                        self.fileString = try! String(contentsOf: url!, encoding: .utf8)
                    }
                } else {
                    self.urlString = url!.absoluteString
                }
            }
          })

          attachment.loadItem(forTypeIdentifier: contentTypeText, options: nil, completionHandler: { (results, error) in
            if results != nil {
                let text = results as! String
                self.textString = text
                _ = self.isContentValid()
            }
          })

      }
    }

    @objc func openURL(_ url: URL) -> Bool {
        var responder: UIResponder? = self
        while responder != nil {
            if let application = responder as? UIApplication {
                return application.perform(#selector(openURL(_:)), with: url) != nil
            }
            responder = responder?.next
        }
        return false
    }

}
```

The share extension is like a little standalone program, so to get to your app the extension has to make an openURL call. In order to make your app reachable by a URL, you have to define a URL scheme ([Register Your URL Scheme](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app)). The code above calls a URL scheme named "myScheme" (first line in "didSelectPost"), so just replace this with your scheme.

Add the pod `FBSDKCoreKit` to `ios/App/Podfile`:

```diff
platform :ios, '12.0'
use_frameworks!

# workaround to avoid Xcode caching of Pods that requires
# Product -> Clean Build Folder after new Cordova plugins installed
# Requires CocoaPods 1.6 or newer
install! 'cocoapods', :disable_input_output_paths => true

def capacitor_pods
  pod 'Capacitor', :path => '../../node_modules/@capacitor/ios'
  pod 'CapacitorCordova', :path => '../../node_modules/@capacitor/ios'
  pod 'SendIntent', :path => '../../../..'
end

target 'App' do
  capacitor_pods
  # Add your Pods here
+ pod 'FBSDKCoreKit'
end
```

And then run `pod install` in that folder.

Finally, in your AppDelegate.swift, override the following function like this:

```swift
import SendIntent
import FBSDKCoreKit

// ...

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    // ...

    let store = ShareStore.store

    // ...

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {

        var success = true
        if CAPBridge.handleOpenUrl(url, options) {
        success = FBSDKCoreKit.ApplicationDelegate.shared.application(app, open: url, options: options)
        }

        guard let components = NSURLComponents(url: url, resolvingAgainstBaseURL: true),
            let params = components.queryItems else {
                return false
        }
        store.text = params.first(where: { $0.name == "text" })?.value as! String
        store.url = params.first(where: { $0.name == "url" })?.value as! String
        store.image = params.first(where: { $0.name == "image" })?.value as! String
        store.file = params.first(where: { $0.name == "file" })?.value?.removingPercentEncoding as! String
        store.processed = false
        let nc = NotificationCenter.default
        nc.post(name: Notification.Name("triggerSendIntent"), object: nil )

        return success
    }

    // ...

}
```

This is the function started when an application is open by URL.

Also, make sure you use SendIntent as a listener. Otherwise you will miss the event fired in the plugin:

```js
window.addEventListener("sendIntentReceived", () => {
    Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
        if (result.text) {
            // ...
        }
    });
})
```
