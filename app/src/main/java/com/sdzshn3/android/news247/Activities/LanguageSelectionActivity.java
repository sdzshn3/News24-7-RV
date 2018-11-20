package com.sdzshn3.android.news247.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sdzshn3.android.news247.PrefManager;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder.holder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LanguageSelectionActivity extends AppCompatActivity {

    PrefManager prefManager;
    @BindView(R.id.english_button)
    Button englishButton;
    @BindView(R.id.telugu_button)
    Button teluguButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @BindView(R.id.choose_language_title)
    TextView title;
    @BindView(R.id.change_language_hint)
    TextView languageHint;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);
        ButterKnife.bind(this);


        Typeface semiBoldText = Typeface.createFromAsset(getAssets(), "GoogleSans-Medium.ttf");
        Typeface regularText = Typeface.createFromAsset(getAssets(), "GoogleSans-Regular.ttf");

        title.setTypeface(semiBoldText);
        languageHint.setTypeface(regularText);

        sharedPreferences = this.getSharedPreferences(holder.LANGUAGE_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        prefManager = new PrefManager(this);
        englishButton.setOnClickListener(englishButtonClickListener);
        teluguButton.setOnClickListener(teluguButtonClickListener);
    }

    View.OnClickListener englishButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                MainActivity.activity.finish();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("LanguageSelectionAct", "ignore. Because app launched first time");
            }
            prefManager.setFirstTimeLaunch(false);
            editor.putString(holder.SELECTED_LANGUAGE, holder.english);
            editor.apply();
            Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener teluguButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                MainActivity.activity.finish();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("LanguageSelectionAct", "ignore. Because app launched first time");
            }
            prefManager.setFirstTimeLaunch(false);
            editor.putString(holder.SELECTED_LANGUAGE, holder.telugu);
            editor.apply();
            Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
