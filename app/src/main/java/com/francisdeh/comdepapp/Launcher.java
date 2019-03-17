package com.francisdeh.comdepapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class Launcher extends AppCompatActivity {

    TextView textTitle, comdepText, appText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        textTitle = (TextView)findViewById(R.id.textTitle);
        comdepText = (TextView)findViewById(R.id.comdep_text);
        appText = (TextView)findViewById(R.id.app_text);

        appText.setTypeface(Typeface.createFromAsset(getAssets(), "greecian.ttf"));
        comdepText.setTypeface(Typeface.createFromAsset(getAssets(), "greecian.ttf"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //intent
                Intent intent = new Intent(Launcher.this, Register.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
