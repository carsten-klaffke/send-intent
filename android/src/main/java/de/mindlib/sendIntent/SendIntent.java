package de.mindlib.sendIntent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64OutputStream;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import org.apache.commons.io.IOUtils;

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
                    JSObject ret = new JSObject();
                    ret.put("image", encoder((Uri)intent.getExtras().get(Intent.EXTRA_STREAM)));
                    call.resolve(ret);
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

    private String encoder(Uri imagePath) {


        String base64Image = "";
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(imagePath);) {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            base64Image = java.util.Base64.getEncoder().encodeToString(bytes);
            base64Image = "data:image/jpg;base64," + base64Image;
        } catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
        return base64Image;
    }

}
