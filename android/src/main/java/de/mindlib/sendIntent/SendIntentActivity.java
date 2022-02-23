package de.mindlib.sendIntent;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class SendIntentActivity extends BridgeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPlugin(SendIntent.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();
    }
}
