package com.sdzshn3.android.news247.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sdzshn3.android.news247.SupportClasses.PrefManager;
import com.sdzshn3.android.news247.R;
import com.sdzshn3.android.news247.SupportClasses.DataHolder;

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

        //Text typefaces (styles)
        Typeface semiBoldText = Typeface.createFromAsset(getAssets(), "GoogleSans-Medium.ttf");
        Typeface regularText = Typeface.createFromAsset(getAssets(), "GoogleSans-Regular.ttf");

        //Setting typefaces to textViews
        title.setTypeface(semiBoldText);
        languageHint.setTypeface(regularText);

        //Initialising the language name sharedPreferences and editor
        sharedPreferences = this.getSharedPreferences(DataHolder.LANGUAGE_PREF_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        prefManager = new PrefManager(this);
        englishButton.setOnClickListener(englishButtonClickListener);
        teluguButton.setOnClickListener(teluguButtonClickListener);
    }

    //OnClickListener for English language button
    View.OnClickListener englishButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                //Finishing the MainActivity so that it wont appear again when back button is pressed
                MainActivity.activity.finish();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("LanguageSelectionAct", "ignore. Because app launched first time");
            }
            //Setting first time launch as false because app is launched and setup
            prefManager.setFirstTimeLaunch(false);
            //Saving the language preference to english so that when app launches again, it starts with english language
            editor.putString(DataHolder.SELECTED_LANGUAGE, DataHolder.english);
            editor.apply();
            Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    ////OnClickListener for Telugu language button
    View.OnClickListener teluguButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                //Finishing the MainActivity so that it wont appear again when back button is pressed
                MainActivity.activity.finish();
            }catch (NullPointerException e) {
                e.printStackTrace();
                Log.e("LanguageSelectionAct", "ignore. Because app launched first time");
            }
            //Setting first time launch as false because app is launched and setup
            prefManager.setFirstTimeLaunch(false);
            //Saving the language preference to telugu so that when app launches again, it starts with telugu language
            editor.putString(DataHolder.SELECTED_LANGUAGE, DataHolder.telugu);
            editor.apply();
            Intent intent = new Intent(LanguageSelectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
