package com.example.andrevina;

import android.graphics.Bitmap;

public class Record {

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

    public int compareRecords(Record b) {

        int compareAttempts = this.getAttempts() - b.getAttempts();

        if (compareAttempts == 0) {

            int compareTime = this.getGameTime() - b.getGameTime();

            if (compareTime == 0) {

                return 0; // Si coinciden tanto en intentos como en tiempo, decido dejar primero el registro mas antiguo (mantener el orden igual).

            } else {

                return compareTime;

            }

        } else {

            return compareAttempts;

        }

    }

}
