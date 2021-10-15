package de.mindlib.sendIntent;

import android.content.Intent;
import android.net.Uri;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.apache.commons.io.FilenameUtils;

@CapacitorPlugin()
public class SendIntent extends Plugin {

    @PluginMethod
    public void checkSendIntentReceived(PluginCall call) {
        Intent intent = bridge.getActivity().getIntent();
        if (!"intent handled".equals(intent.getAction())) {
            String action = intent.getAction();
            String type = intent.getType();
            intent.setAction("intent handled");
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                JSObject ret = new JSObject();
                String title = intent.getStringExtra(Intent.EXTRA_TEXT);
                Uri uri = intent.getClipData().getItemAt(0).getUri();

                if(title == null && uri !=null)
                    title = FilenameUtils.getName(uri.getPath());

                ret.put("title", title);
                ret.put("description", null);
                ret.put("type", type);
                ret.put("url", uri.toString());
                call.resolve(ret);
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
