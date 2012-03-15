package com.bpellow.android.boilerplate.activity;

import android.app.Activity;
import android.os.Bundle;

import com.bpellow.android.boilerplate.R;

public class SplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}