package com.example.musicpractice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnPreparedListener {
    // Actions associated with Intents that start this Service
    public static final String ACTION_PLAY = "com.example.musicpractice.PLAY";
    public static final String ACTION_PAUSE = "com.example.musicpractice.PAUSE";

    private MediaPlayer mMediaPlayer;
    private boolean mPrepared = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private Uri resourceToUri(int resId) {
        Resources res = getResources();
        return new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(res.getResourcePackageName(resId))
                .appendPath(res.getResourceTypeName(resId))
                .appendPath(res.getResourceEntryName(resId)).build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == ACTION_PLAY) {
            play();
        } else if (intent.getAction() == ACTION_PAUSE) {
            pause();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void pause() {
    }

    private void play() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPrepared = true;
    }
}
