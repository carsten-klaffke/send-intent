package de.mindlib.sendIntent;

import android.content.Intent;
import android.net.Uri;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@NativePlugin()
public class SendIntent extends Plugin {

    @PluginMethod
    public void checkSendIntentReceived(PluginCall call) {

        Intent intent = bridge.getActivity().getIntent();
        if (!"intent handled".equals(intent.getAction())) {
            String action = intent.getAction();
            String type = intent.getType();
            intent.setAction("intent handled");
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    JSObject ret = new JSObject();
                    String stringExtra = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (stringExtra == null) {
                        if (intent.getClipData() != null &&
                                intent.getClipData().getItemAt(0) != null &&
                                intent.getClipData().getItemAt(0).getUri() != null)
                            try {
                                ret.put("file", getStringFromFile(getContext().getContentResolver().openInputStream(intent.getClipData().getItemAt(0).getUri())));
                                call.resolve(ret);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    } else
                        ret.put("text", stringExtra);
                    call.resolve(ret);
                } else if (type.startsWith("image/")) {
                    JSObject ret = new JSObject();
                    ret.put("image", encoder((Uri) intent.getExtras().get(Intent.EXTRA_STREAM)));
                    call.resolve(ret);
                } else if (type.equals("application/octet-stream")) {
                    JSObject ret = new JSObject();
                    if (intent.getClipData() != null &&
                            intent.getClipData().getItemAt(0) != null &&
                            intent.getClipData().getItemAt(0).getUri() != null)
                        try {
                            ret.put("file", getStringFromFile(getContext().getContentResolver().openInputStream(intent.getClipData().getItemAt(0).getUri())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile(InputStream io) throws Exception {
        String ret = convertStreamToString(io);
        io.close();
        return ret;
    }

}
