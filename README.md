# Send-Intent

This is a Capacitor plugin meant to be used in Ionic applications for checking if your App was targeted as a share goal. It supports both Android and iOS and is able to handle a single file or multiple files of any type.

Check out my app [mindlib - your personal mind library](https://play.google.com/store/apps/details?id=de.mindlib) to see it in action.

## Projects below Capacitor 3

For projects below Capacitor 3 please use "send-intent": "1.1.7"!

## Installation

```
npm install send-intent
npx cap sync
```

## Usage

Import & Sample call

Shared files will be received as URI-String. You can use Capacitor's [Filesystem](https://capacitorjs.com/docs/apis/filesystem) plugin to get the files content. 
The "url"-property of the SendIntent result is also used for web urls, e.g. when sharing a website via browser, so it is not necessarily a file path. Make sure to handle this
either through checking the "type"-property or by error handling!

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

Configure a new activity in AndroidManifest.xml!

```xml
<!-- Your actual activity declaration -->
<activity
      android:name=".MainActivity"
    ...
    ...
    ...>
</activity>

<!-- Add new One Activity for handle Intent here -->
<activity
        android:name="de.mindlib.sendIntent.SendIntentActivity" <!-- Do not change this name, otherwise your application will crash -->
        android:label="@string/app_name"
        android:exported="true"
        android:theme="@style/AppTheme.NoActionBar">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />

        <!-- Depending on your project, you can add here the type of data you wish to receive -->
        <data android:mimeType="text/plain" />
        <data android:mimeType="image/*" />
        <data android:mimeType="application/*" />
        <data android:mimeType="video/*" />
    </intent-filter>
</activity>

```

On Android, I strongly recommend closing the send-intent-activity after you have processed the send-intent in your app. Not doing 
this can lead to app state issues (because you have two instances running) or trigger the same intent again if your app 
reloads from idle mode. You can close the send-intent-activity by calling the "finish"-method:

```js
SendIntent.finish();
```

However, if you want to stay in your app after the send-intent, there is a solution using Deep Links which was worked out here: https://github.com/carsten-klaffke/send-intent/issues/69#issuecomment-1544619608

## **iOS**

Create a "Share Extension" ([Creating an App extension](https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionCreation.html#//apple_ref/doc/uid/TP40014214-CH5-SW1)) and make sure that the extensions "iOS deployment target" version is in sync with your apps deployment target version! 

Set the activation rules in the extensions Info.plist, so that your app will be displayed as share option!

```
...
    <key>NSExtensionActivationRule</key>
    <dict>
        <key>NSExtensionActivationSupportsFileWithMaxCount</key>
        <integer>5</integer>
        <key>NSExtensionActivationSupportsImageWithMaxCount</key>
        <integer>5</integer>
        <key>NSExtensionActivationSupportsMovieWithMaxCount</key>
        <integer>5</integer>
        <key>NSExtensionActivationSupportsText</key>
        <true/>
        <key>NSExtensionActivationSupportsWebPageWithMaxCount</key>
        <integer>1</integer>
        <key>NSExtensionActivationSupportsWebURLWithMaxCount</key>
        <integer>1</integer>
        <key>NSExtensionActivationUsesStrictMatching</key>
        <false/>
    </dict>
...            
```

Code for the ShareViewController:

```swift
//
//  ShareViewController.swift
//  mindlib
//
//  Created by Carsten Klaffke on 05.07.20.
//

import MobileCoreServices
import Social
import UIKit

class ShareItem {
    
    public var title: String?
    public var type: String?
    public var url: String?
}

class ShareViewController: UIViewController {
    
    private var shareItems: [ShareItem] = []
    
    override public func viewDidAppear(_ animated: Bool) {
       super.viewDidAppear(animated)
       self.extensionContext!.completeRequest(returningItems: [], completionHandler: nil)
    }
    
    private func sendData() {
        let queryItems = shareItems.map {
            [
                URLQueryItem(
                    name: "title",
                    value: $0.title?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""),
                URLQueryItem(name: "description", value: ""),
                URLQueryItem(
                    name: "type",
                    value: $0.type?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""),
                URLQueryItem(
                    name: "url",
                    value: $0.url?.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed) ?? ""),
            ]
        }.flatMap({ $0 })
        var urlComps = URLComponents(string: "YOUR_APP_URL_SCHEME://")!
        urlComps.queryItems = queryItems
        openURL(urlComps.url!)
    }
    
    fileprivate func createSharedFileUrl(_ url: URL?) -> String {
        let fileManager = FileManager.default
        
        let copyFileUrl =
        fileManager.containerURL(forSecurityApplicationGroupIdentifier: "YOUR_APP_GROUP_ID")!
            .absoluteString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)! + url!
            .lastPathComponent.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        try? Data(contentsOf: url!).write(to: URL(string: copyFileUrl)!)
        
        return copyFileUrl
    }
    
    func saveScreenshot(_ image: UIImage, _ index: Int) -> String {
        let fileManager = FileManager.default
        
        let copyFileUrl =
        fileManager.containerURL(forSecurityApplicationGroupIdentifier: "group.SendIntentExample")!
            .absoluteString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)!
        + "/screenshot_\(index).png"
        do {
            try image.pngData()?.write(to: URL(string: copyFileUrl)!)
            return copyFileUrl
        } catch {
            print(error.localizedDescription)
            return ""
        }
    }
    
    fileprivate func handleTypeUrl(_ attachment: NSItemProvider)
    async throws -> ShareItem
    {
        let results = try await attachment.loadItem(forTypeIdentifier: kUTTypeURL as String, options: nil)
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
        
        return shareItem
    }
    
    fileprivate func handleTypeText(_ attachment: NSItemProvider)
    async throws -> ShareItem
    {
        let results = try await attachment.loadItem(forTypeIdentifier: kUTTypeText as String, options: nil)
        let shareItem: ShareItem = ShareItem()
        let text = results as! String
        shareItem.title = text
        shareItem.type = "text/plain"
        return shareItem
    }
    
    fileprivate func handleTypeMovie(_ attachment: NSItemProvider)
    async throws -> ShareItem
    {
        let results = try await attachment.loadItem(forTypeIdentifier: kUTTypeMovie as String, options: nil)
        let shareItem: ShareItem = ShareItem()
        
        let url = results as! URL?
        shareItem.title = url!.lastPathComponent
        shareItem.type = "video/" + url!.pathExtension.lowercased()
        shareItem.url = createSharedFileUrl(url)
        return shareItem
    }
    
    fileprivate func handleTypeImage(_ attachment: NSItemProvider, _ index: Int)
    async throws -> ShareItem
    {
        let data = try await attachment.loadItem(forTypeIdentifier: kUTTypeImage as String, options: nil)
        
        let shareItem: ShareItem = ShareItem()
            switch data {
                case let image as UIImage:
                    shareItem.title = "screenshot_\(index)"
                    shareItem.type = "image/png"
                    shareItem.url = self.saveScreenshot(image, index)
                case let url as URL:
                    shareItem.title = url.lastPathComponent
                    shareItem.type = "image/" + url.pathExtension.lowercased()
                    shareItem.url = self.createSharedFileUrl(url)
                default:
                    print("Unexpected image data:", type(of: data))
        }
        return shareItem
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        shareItems.removeAll()
        
        let extensionItem = extensionContext?.inputItems[0] as! NSExtensionItem
        Task {
            try await withThrowingTaskGroup(
                of: ShareItem.self,
                body: { taskGroup in
                    
                    for (index, attachment) in extensionItem.attachments!.enumerated() {
                        if attachment.hasItemConformingToTypeIdentifier(kUTTypeURL as String) {
                            taskGroup.addTask {
                                return try await self.handleTypeUrl(attachment)
                            }
                        } else if attachment.hasItemConformingToTypeIdentifier(kUTTypeText as String) {
                            taskGroup.addTask {
                                return try await self.handleTypeText(attachment)
                            }
                        } else if attachment.hasItemConformingToTypeIdentifier(kUTTypeMovie as String) {
                            taskGroup.addTask {
                                return try await self.handleTypeMovie(attachment)
                            }
                        } else if attachment.hasItemConformingToTypeIdentifier(kUTTypeImage as String) {
                            taskGroup.addTask {
                                return try await self.handleTypeImage(attachment, index)
                            }
                        }
                    }
                    
                    for try await item in taskGroup {
                        self.shareItems.append(item)
                    }
                })
            
            self.sendData()
            
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

The share extension is like a little standalone program, so to get to your app the extension has to make an openURL call. In order to make your app reachable by a URL, you have to define a URL scheme ([Register Your URL Scheme](https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app)). The code above calls a URL scheme named "YOUR_APP_URL_SCHEME" (first line in "didSelectPost"), so just replace this with your scheme!
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

Make sure to register the following event-listener! Otherwise you will miss the event fired in the plugin:

```js
window.addEventListener("sendIntentReceived", () => {
    Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
        if (result) {
            // ...
        }
    });
})
```

You should also exceute a call on app startup as described in [Usage](#usage), because on a cold start the event-listener might not be registered early enough (see [https://github.com/carsten-klaffke/send-intent/issues/57]).

## Donation

If you want to support my work, you can donate me via Lightning (Bitcoin) or Stripe.

Lightning: mostroll13@walletofsatoshi.com

[Donate me a coffee on Stripe](https://buy.stripe.com/5kA9EH5SAe778VO146)

## Consulting

I work as a freelance IT Consultant. If you need help with this plugin or want me to do the complete setup in your App, you can contact me at mail@carsten-klaffke.de.


