package de.mindlib.sendIntent;

import android.content.Intent;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

@NativePlugin()
public class SendIntent extends Plugin {

    @PluginMethod
    public void checkSendIntentReceived(PluginCall call) {

        Intent intent = bridge.getActivity().getIntent();
        if (!"intent handled".equals(intent.getAction())) {
            String action = intent.getAction();
            String type = intent.getType();
            String stringExtra = intent.getStringExtra(Intent.EXTRA_TEXT);
            intent.setAction("intent handled");
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    JSObject ret = new JSObject();
                    ret.put("text", stringExtra);
                    call.resolve(ret);
                } else if (type.startsWith("image/")) {
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                if (type.startsWith("image/")) {

                }
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }
        call.reject("No processing needed");
    }

}
