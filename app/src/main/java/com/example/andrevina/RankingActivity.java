package com.example.andrevina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class RankingActivity extends AppCompatActivity {

    ArrayList<Record> records = new ArrayList<Record>();

    String nickname;
    int attempts;
    int gameTime;
    Bitmap photo;
    String timeInfo;

    File recordsDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        getExtras(extras);

        checkFiles();



    }

    public void getExtras(Bundle extras) {

        nickname = extras.getString("NICKNAME");
        attempts = extras.getInt("ATTEMPTS");
        gameTime = extras.getInt("GAME_TIME");
        photo = extras.getParcelable("PHOTO");
        timeInfo = extras.getString("TIME_INFO");

    }

    public void checkFiles() {

        recordsDir = new File (getApplicationContext().getFilesDir(),"records");

        if (!recordsDir.exists()) {
            recordsDir.mkdir();
        }

    }

    public void returnToMain(View v) {

        Intent intent = new Intent(RankingActivity.this, MainActivity.class);

        startActivity(intent);

    }
}