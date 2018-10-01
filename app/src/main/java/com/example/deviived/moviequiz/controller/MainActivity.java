package com.example.deviived.moviequiz.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.deviived.moviequiz.R;
import com.example.deviived.moviequiz.model.User;

public class MainActivity extends AppCompatActivity {

    private TextView mGreetingText;
    private EditText mNameInput;
    private Button mPlayButton;
    private User mUser;
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private SharedPreferences mPreferences;
    public static final String PREF_KEY_SCORE = "PREF_KEY_SCORE";
    public static final String PREF_KEY_FIRSTNAME = "PREF_KEY_FIRSTNAME";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mGreetingText = (TextView) findViewById(R.id.activity_main_greeting_txt);
        mNameInput = (EditText) findViewById(R.id.activity_main_name_input);
        mPlayButton = (Button) findViewById(R.id.activity_main_play_btn);
        mUser = new User();
        mPlayButton.setEnabled(false);
        mPreferences = getPreferences(MODE_PRIVATE);
        greetUser();

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlayButton.setEnabled(s.toString().length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.setFirstname(mNameInput.getText().toString());
                mPreferences.edit().putString(PREF_KEY_FIRSTNAME, mUser.getFirstname()).apply();
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Fetch the score from the Intent
            int score = data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0);
            mPreferences.edit().putInt(PREF_KEY_SCORE, score).apply();
            greetUser();
        }
    }

    private void greetUser() {
        String firstname = mPreferences.getString(PREF_KEY_FIRSTNAME, null);

        if (null != firstname) {
            int score = mPreferences.getInt(PREF_KEY_SCORE, 0);

            String fulltext = "Welcome back, " + firstname
                    + "!\nYour last score was " + score
                    + ", will you do better this time?";
            mGreetingText.setText(fulltext);
            mNameInput.setText(firstname);
            mNameInput.setSelection(firstname.length());
            mPlayButton.setEnabled(true);
        }
    }



}
