package com.example.andrevina;

import android.graphics.Bitmap;

import java.util.Comparator;

public class Record implements Comparator<Record> {

    int attempts;
    String nickname;
    int gameTime;
    Bitmap photo;
    String timeInfo;

    Record(int attempts, String nickname, int gameTime, Bitmap photo, String timeInfo) {

        this.attempts = attempts;
        this.nickname = nickname;
        this.gameTime = gameTime;
        this.photo = photo;
        this.timeInfo = timeInfo;

    }

    public int getAttempts() {
        return attempts;
    }

    public String getNickname() {
        return nickname;
    }

    public int getGameTime() {
        return gameTime;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public String getTimeInfo() {
        return timeInfo;
    }

    public int compare(Record a, Record b) {

        int attemptsComparison = a.getAttempts() - b.getAttempts();

        if (attemptsComparison == 0) {

            int timeComparison = a.getGameTime() - b.getGameTime();

            if (timeComparison == 0) {

                int nicknameComparison = a.getNickname().compareTo(b.getNickname());

                return nicknameComparison;

            } else {

                return timeComparison;

            }

        } else {

            return attemptsComparison;

        }

    }

}
