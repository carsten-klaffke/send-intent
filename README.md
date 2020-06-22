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