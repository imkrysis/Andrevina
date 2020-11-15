package com.example.andrevina;

import android.graphics.Bitmap;

import java.util.Comparator;

public class Record implements Comparator<Record> {

    String nickname;
    int attempts;
    int gameTime;
    Bitmap photoBitmap;
    String timeInfo;

    Record(String nickname, int attempts, int gameTime, Bitmap photoBitmap, String timeInfo) {

        this.nickname = nickname;
        this.attempts = attempts;
        this.gameTime = gameTime;
        this.photoBitmap = photoBitmap;
        this.timeInfo = timeInfo;

    }

    public String getNickname() {
        return nickname;
    }

    public int getAttempts() {
        return attempts;
    }

    public int getGameTime() {
        return gameTime;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public String getTimeInfo() {
        return timeInfo;
    }

    public int compare(Record record1, Record record2) {

        int attemptsComparison = record1.getAttempts() - record2.getAttempts();

        if (attemptsComparison == 0) {

            int timeComparison = record1.getGameTime() - record2.getGameTime();

            if (timeComparison == 0) {

                return 0;

            } else {

                return timeComparison;

            }

        } else {

            return attemptsComparison;

        }

    }

}
