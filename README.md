# Send-Intent

This is a small Capacitor plugin meant to be used in Ionic applications for checking if your App was targeted as a share goal. It supports both Android and iOS. So far, it checks and returns "SEND"-intents of mimeType "text/plain", "image" or "application/octet-stream" (files).

Check out my app [mindlib - your personal mind library](https://play.google.com/store/apps/details?id=de.mindlib) to see it in action.

## Projects below Capacitor 3

For projects below Capacitor 3 please use  "send-intent": "1.1.7".

## Installation

```
npm install send-intent
npx cap sync
```

## Usage

Import & Sample call

Shared files will be received as URI-String. You can use Capacitor's [Filesystem](https://capacitorjs.com/docs/apis/filesystem) plugin to get the files content. 
The "url"-property of the SendIntent result is also used for web urls, e.g. when sharing a website via browser, so it is not necessarily a file path. Make sure to handle this
either through checking the "type"-property or by error handling.

```js
import {SendIntent} from "send-intent";

SendIntent.checkSendIntentReceived().then((result: any) => {
    if (result) {
        console.log('SendIntent received');
        console.log(JSON.stringify(result));
    }
    if (result.url) {
        let resultUrl = decodeURIComponent(result.url);
        Filesystem.readFile({path: resultUrl})
        .then((content) => {
            console.log(content.data);
        })
        .catch((err) => console.error(err));
    }
}).catch(err => console.error(err));
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
        if (result) {
            // ...
        }
    });
})
```

Using SendIntent as a listener can be useful if the intent doesn't trigger a rerender of your app.

## **iOS**

Create a "Share Extension" ([Creating an App extension](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionCreation.html#//apple_ref/doc/uid/TP40014214-CH5-SW1))

Code for the ShareViewController:

```swift
//
//  ShareViewController.swift
//  mindlib
//
//  Created by Carsten Klaffke on 05.07.20.
//

import UIKit
import Social
import MobileCoreServices

class ShareItem {
    
       public var title: String?
       public var type: String?
       public var url: String?
}

class ShareViewController: SLComposeServiceViewController {

    private var shareItems: [ShareItem] = []
    
    override func isContentValid() -> Bool {
        // Do validation of contentText and/or NSExtensionContext attachments here
        print(contentText ?? "content is empty")
        return true
    }

    override func didSelectPost() {
        let queryItems = shareItems.map { [URLQueryItem(name: "title", value: $0.title?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""), URLQueryItem(name: "description", value: self.contentText?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""), URLQueryItem(name: "type", value: $0.type?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""), URLQueryItem(name: "url", value: $0.url?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? "")] }.flatMap({ $0 })
        var urlComps = URLComponents(string: "YOUR_APP_URL_SCHEME://")!
        urlComps.queryItems = queryItems
        openURL(urlComps.url!)
        self.extensionContext!.completeRequest(returningItems: [], completionHandler: nil)
    }

    override func configurationItems() -> [Any]! {
        // To add configuration options via table cells at the bottom of the sheet, return an array of SLComposeSheetConfigurationItem here.
        return []
    }

    fileprivate func createSharedFileUrl(_ url: URL?) -> String {
        let fileManager = FileManager.default

        let copyFileUrl = fileManager.containerURL(forSecurityApplicationGroupIdentifier: "YOUR_APP_GROUP_ID")!.absoluteString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)! + "/" + url!.lastPathComponent.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        try? Data(contentsOf: url!).write(to: URL(string: copyFileUrl)!)

        return copyFileUrl
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        
        shareItems.removeAll()
        
        let extensionItem = extensionContext?.inputItems[0] as! NSExtensionItem
        let contentTypeURL = kUTTypeURL as String
        let contentTypeText = kUTTypeText as String
        let contentTypeMovie = kUTTypeMovie as String
        let contentTypeImage = kUTTypeImage as String
       
        for attachment in extensionItem.attachments as! [NSItemProvider] {
            
            if attachment.hasItemConformingToTypeIdentifier(contentTypeURL) {
                attachment.loadItem(forTypeIdentifier: contentTypeURL, options: nil, completionHandler: { [self] (results, error) in
                    if results != nil {
                        let url = results as! URL?
                        let shareItem: ShareItem = ShareItem()
                        
                        if url!.isFileURL {
                            shareItem.title = url!.lastPathComponent
                            shareItem.type = "application/" + url!.pathExtension.lowercased()
                            shareItem.url = createSharedFileUrl(url)
                        } else {
                            shareItem.title = url!.absoluteString
                            shareItem.url = url!.absoluteString
                            shareItem.type = "text/plain"
                        }
                        
                        self.shareItems.append(shareItem)
                        
                    }
                })
            } else if attachment.hasItemConformingToTypeIdentifier(contentTypeText) {
                attachment.loadItem(forTypeIdentifier: contentTypeText, options: nil, completionHandler: { (results, error) in
                    if results != nil {
                        let shareItem: ShareItem = ShareItem()
                        let text = results as! String
                        shareItem.title = text
                        _ = self.isContentValid()
                        shareItem.type = "text/plain"
                        self.shareItems.append(shareItem)
                    }
                })
            } else if attachment.hasItemConformingToTypeIdentifier(contentTypeMovie) {
                attachment.loadItem(forTypeIdentifier: contentTypeMovie, options: nil, completionHandler: { [self] (results, error) in
                    if results != nil {
                        let shareItem: ShareItem = ShareItem()
                        
                        let url = results as! URL?
                        shareItem.title = url!.lastPathComponent
                        shareItem.type = "video/" + url!.pathExtension
                        shareItem.url = createSharedFileUrl(url)
                        self.shareItems.append(shareItem)
                    }
                })
            } else if attachment.hasItemConformingToTypeIdentifier(contentTypeImage) {
                attachment.loadItem(forTypeIdentifier: contentTypeImage, options: nil, completionHandler: { [self] (results, error) in
                    if results != nil {
                        let shareItem: ShareItem = ShareItem()
                        
                        let url = results as! URL?
                        shareItem.title = url!.lastPathComponent
                        shareItem.type = "image/" + url!.pathExtension
                        shareItem.url = createSharedFileUrl(url)
                        self.shareItems.append(shareItem)
                    }
                })
            }
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

The share extension is like a little standalone program, so to get to your app the extension has to make an openURL call. In order to make your app reachable by a URL, you have to define a URL scheme ([Register Your URL Scheme](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app)). The code above calls a URL scheme named "YOUR_APP_URL_SCHEME" (first line in "didSelectPost"), so just replace this with your scheme.
To allow sharing of files between the extension and your main app, you need to [create an app group](https://developer.apple.com/documentation/bundleresources/entitlements/com_apple_security_application-groups) which is checked for both your extension and main app. Replace "YOUR_APP_GROUP_ID" in "setSharedFileUrl()" with your app groups name.

Finally, in your AppDelegate.swift, override the following function like this:

```swift
import SendIntent
import Capacitor

// ...

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    // ...

    let store = ShareStore.store

    // ...

    func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
            
            var success = true
            if CAPBridge.handleOpenUrl(url, options) {
                success = ApplicationDelegateProxy.shared.application(app, open: url, options: options)
            }
            
            guard let components = NSURLComponents(url: url, resolvingAgainstBaseURL: true),
                  let params = components.queryItems else {
                      return false
                  }
            let titles = params.filter { $0.name == "title" }
            let descriptions = params.filter { $0.name == "description" }
            let types = params.filter { $0.name == "type" }
            let urls = params.filter { $0.name == "url" }
            
            store.shareItems.removeAll()
        
            if(titles.count > 0){
                for index in 0...titles.count-1 {
                    var shareItem: JSObject = JSObject()
                    shareItem["title"] = titles[index].value!
                    shareItem["description"] = descriptions[index].value!
                    shareItem["type"] = types[index].value!
                    shareItem["url"] = urls[index].value!
                    store.shareItems.append(shareItem)
                }
            }
            
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
        if (result) {
            // ...
        }
    });
})
```

## Donation

If you want to support my work, you can donate me on bitcoin:bc1q60ntnlz4wqfup3yg3hyqmzfkuraf8clmvupqvs
