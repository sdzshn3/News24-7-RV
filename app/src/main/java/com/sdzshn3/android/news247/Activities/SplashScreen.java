package com.sdzshn3.android.news247.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.sdzshn3.android.news247.R;

import io.fabric.sdk.android.Fabric;

public class SplashScreen extends AppCompatActivity {

    /**This activity is for showing splash screen without post delay*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);

        //Setting up Crashlytics and Fabric
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());

        Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
