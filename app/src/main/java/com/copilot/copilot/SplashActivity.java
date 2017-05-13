package com.copilot.copilot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Akash on 2017-05-12.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent i=new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 5000);
    }
}