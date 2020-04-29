This is a small Capacitor plugin meant to be used in Ionic applications for checking if an Android "SEND"-intent was received. So far, it checks and returns only "SEND"-intents of type "text/plain".

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
            // Additional plugins you've installed go here
            // Ex: add(TotallyAwesomePlugin.class);
            add(SendIntent.class);
        }});
    }

}
```
