package de.mindlib.sendIntent;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;
import java.util.List;

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
                call.resolve(readItemAt(intent, type, 0));
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
                JSObject ret = readItemAt(intent, type, 0);
                List additionalItems = new ArrayList<JSObject>();

                for (int index = 1; index < intent.getClipData().getItemCount(); index++) {
                    additionalItems.add(readItemAt(intent, type, index));
                }
                ret.put("additionalItems", new JSArray(additionalItems));
                call.resolve(ret);
            }
        }
        call.reject("No processing needed");
    }

    private JSObject readItemAt(Intent intent, String type, int index) {
        JSObject ret = new JSObject();
        String title = intent.getStringExtra(Intent.EXTRA_TEXT);
        Uri uri = intent.getClipData().getItemAt(index).getUri();

        if (title == null && uri != null)
            title = readFileName(uri);

        ret.put("title", title);
        ret.put("description", null);
        ret.put("type", type);
        ret.put("url", uri.toString());
        return ret;
    }

    public String readFileName(Uri uri) {
        Cursor returnCursor =
                getContext().getContentResolver().query(uri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        returnCursor.moveToFirst();
        return returnCursor.getString(returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
    }

}
