This is a small Capacitor plugin meant to be used in Ionic applications for checking if an Android "SEND"-intent was received. So far, it checks and returns "SEND"-intents of type "text/plain" or "image".

Check out my app <a href="https://play.google.com/store/apps/details?id=de.mindlib">mindlib - your personal mind library</a> to see it in action.

<b>Usage:</b>

Register in JS
```
import {registerWebPlugin} from "@capacitor/core";
import {SendIntent} from "send-intent";
registerWebPlugin(SendIntent);
```

Sample call
```
import {Plugins} from '@capacitor/core';
const {SendIntent} = Plugins;

Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
                if (result.text) {
                    ...
                }
               });
```
Android:

Configure AndroidManifest.xml
```
<activity
    android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale"
    android:name="io.ionic.starter.MainActivity"
    android:label="@string/title_activity_main"
    android:theme="@style/AppTheme.NoActionBarLaunch"
    android:launchMode="singleTask">

    <intent-filter>
        <action android:name="android.intent.action.SEND" />

        <category android:name="android.intent.category.DEFAULT" />

        <data android:mimeType="text/plain" />
    </intent-filter>
</activity>
```

Register in activity
```
public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializes the Bridge
        this.init(savedInstanceState, new ArrayList<Class<? extends Plugin>>() {{
            add(SendIntent.class);
        }});
    }

}
```
If you want to use checkIntent as a listener, you need to add the following code to your MainActivity:
```
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
```
window.addEventListener("sendIntentReceived", () => {
   Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
                   if (result.text) {
                       ...
                   }
                  });
            })
```

Using SendIntent as a listener can be useful if the intent doesn't trigger a rerender of your app. 

iOS:

Create a share extension (<a href='https://developer.apple.com/library/archive/documentation/General/Conceptual/ExtensibilityPG/ExtensionCreation.html#//apple_ref/doc/uid/TP40014214-CH5-SW1'>Creating an App extension</a>)

Code for the ShareViewController:
```
import UIKit
import Social
import MobileCoreServices

class ShareViewController: SLComposeServiceViewController {
    
    private var urlString: String?
    private var textString: String?
    private var imageString: String?
    
    override func isContentValid() -> Bool {
        // Do validation of contentText and/or NSExtensionContext attachments here
        print(contentText ?? "content is empty")
        return true
    }

    override func didSelectPost() {
        var urlString = "myScheme://?text=" + (self.textString ?? "");
        urlString = urlString + "&url=" + (self.urlString ?? "");
        urlString = urlString + "&image=" + (self.imageString ?? "");
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
                self.urlString = url!.absoluteString
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
The share extension is like a little standalone program, so to get to your app the extension has to make an openURL call. In order to make your app reachable by a URL, you have to define a URL scheme (<a href='https://developer.apple.com/documentation/uikit/inter-process_communication/allowing_apps_and_websites_to_link_to_your_content/defining_a_custom_url_scheme_for_your_app'>Register Your URL Scheme</a>). The code above calls a URL scheme named "myScheme" (first line in "didSelectPost"), so just replace this with your scheme.

Finally, in your AppDelegate.swift, override the following function like this:

```
func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
     
    guard let components = NSURLComponents(url: url, resolvingAgainstBaseURL: true),
        let params = components.queryItems else {
            return false
    }
    store.text = params.first(where: { $0.name == "text" })?.value as! String
    store.url = params.first(where: { $0.name == "url" })?.value as! String
    store.image = params.first(where: { $0.name == "image" })?.value as! String
    store.processed = false
    let nc = NotificationCenter.default
    nc.post(name: Notification.Name("triggerSendIntent"), object: nil )
    
    return success
    }
```
This is the function started when an application is open by URL.

Also, make sure you use SendIntent as a listener. Otherwise you will miss the event fired in the plugin:
```
window.addEventListener("sendIntentReceived", () => {
   Plugins.SendIntent.checkSendIntentReceived().then((result: any) => {
                   if (result.text) {
                       ...
                   }
                  });
            })
```
