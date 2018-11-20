package com.sdzshn3.android.news247.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.sdzshn3.android.news247.R;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);

        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());

        Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
