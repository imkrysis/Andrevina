package com.example.andrevina;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    int randNum;

    int attempts;
    String nickname;
    int gameTime;
    Bitmap photo;
    String timeInfo;

    long startTime;
    long endTime;

    Toast toast;
    Context toastContext;
    CharSequence toastText;
    int toastDuration;

    AlertDialog.Builder adb;
    AlertDialog adRanking;

    EditText editTextNickname;

    public void restart() {

        randNum = (int) (Math.random() * 100 + 1);
        randNum = 7;
        attempts = 0;
        startTime = System.currentTimeMillis();
        endTime = 0;
        nickname = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastContext = getApplicationContext();
        toastDuration = Toast.LENGTH_SHORT;

        final EditText editTextNumber = findViewById(R.id.editTextNumber);
        final Button buttonCheck = findViewById(R.id.buttonCheck);

        setRankingDialog();

        restart();

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!editTextNumber.getText().toString().equalsIgnoreCase("")) {

                    int userNum = Integer.parseInt(editTextNumber.getText().toString());

                    if (userNum >= 1 && userNum <=100) {

                        if (userNum > randNum) {
                            attempts++;

                            toastText = "The number is smaller than " + userNum + ". You have made " + attempts + " attempts.";

                        } else if (userNum < randNum) {
                            attempts++;

                            toastText = "The number is bigger than " + userNum + ". You have made " + attempts + " attempts.";

                        } else {
                            attempts++;

                            endTime = System.currentTimeMillis();

                            setGameTime();

                            toastText = "You guessed the number, it was " + randNum + ". You needed " + attempts + " attempts and " + timeInfo + " minutes/seconds.";

                            showRankingDialog();

                        }

                    } else {

                        toastText = "Introduce a number between 1 and 100, both included.";
                    }

                } else {

                    toastText = "Introduce a number.";
                }

                editTextNumber.setText("");

                toast = Toast.makeText(toastContext, toastText, toastDuration);
                toast.show();

            }
        });

    }

    public void setGameTime() {

        gameTime = (int) (endTime - startTime) / 1000;

        int seconds = gameTime % 60;
        int minutes = gameTime / 60;

        if (minutes < 10) {
            timeInfo = "0" + Integer.toString(minutes);
        } else {
            timeInfo = Integer.toString(minutes);
        }

        timeInfo += ":";

        if (seconds < 10) {
            timeInfo += "0" + Integer.toString(seconds);
        } else {
            timeInfo += Integer.toString(seconds);
        }

    }

    public void setRankingDialog() {

        adb = new AlertDialog.Builder(this);

        adb.setTitle("Do you want to enter your score?");
        adb.setMessage("Introduce your nickname");

        editTextNickname = new EditText(this);

        adb.setView(editTextNickname);

        adb.setPositiveButton("Yes", null);
        adb.setNegativeButton("No", null);

        adRanking = adb.create();

        adRanking.setCancelable(false);
        adRanking.setCanceledOnTouchOutside(false);

    }

    public void showRankingDialog() {

        adRanking.show();

        Button positiveButton = adRanking.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!editTextNickname.getText().toString().equalsIgnoreCase("")) {

                    nickname = editTextNickname.getText().toString();

                    adRanking.dismiss();

                    takePhoto();

                } else {

                    toastText = "Introduce a name.";

                    toast = Toast.makeText(toastContext, toastText, toastDuration);
                    toast.show();

                }

            }
        });

        Button negativeButton = adRanking.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                restart();

                adRanking.dismiss();

            }
        });

    }

    public void takePhoto() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePictureIntent, 1);

//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//
//            startActivityForResult(takePictureIntent, 1);
//
//        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK) {

            Bundle extras = intent.getExtras();
            photo = (Bitmap) extras.get("data");
            openRanking();

        }

    }

    public void openRanking() {

        Intent intent = new Intent(MainActivity.this, RankingActivity.class);

        Bundle extras = new Bundle();

        extras.putString("NICKNAME", nickname);
        extras.putInt("ATTEMPTS", attempts);
        extras.putInt("GAME_TIME", gameTime);
        extras.putParcelable("PHOTO", photo);
        extras.putString("TIME_INFO", timeInfo);

        intent.putExtras(extras);

        startActivity(intent);
    }

}