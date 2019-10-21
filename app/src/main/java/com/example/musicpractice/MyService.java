package com.example.musicpractice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MyService extends Service implements MediaPlayer.OnPreparedListener {
    // Actions associated with Intents that start this Service
    public static final String ACTION_PLAY = "com.example.musicpractice.PLAY";
    public static final String ACTION_PAUSE = "com.example.musicpractice.PAUSE";
    public static final String ACTION_STOP = "com.example.musicpractice.STOP";

    // Channel for notifications.
    private static final String CHANNEL_NAME = "com.example.musicpractice.CHANNEL";

    private MediaPlayer mMediaPlayer;
    private boolean mPrepared = false;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_NAME,
                    "MyService", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
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
        } else if (intent.getAction() == ACTION_STOP) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mPrepared = false;
                stopForeground(true);
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void pause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else if (mPrepared) {
                mMediaPlayer.start();
            }
        }
    }

    private void play() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mMediaPlayer.setAudioAttributes(attributes);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            try {
                mMediaPlayer.setDataSource(this, resourceToUri(R.raw.dead_combo));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "EXCEPTION!", Toast.LENGTH_SHORT).show();
            }

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopForeground(true);
                }
            });
            Toast.makeText(this, "Preparing...", Toast.LENGTH_SHORT).show();
        } else if (mMediaPlayer.isPlaying()) {
            Toast.makeText(this, "Already playing!", Toast.LENGTH_SHORT).show();
        } else if (mPrepared) {
            // The media player is already prepared; we just need to start it again.
            onPrepared(mMediaPlayer);
        } else {
            mMediaPlayer.prepareAsync();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPrepared = true;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_NAME)
                .setContentTitle("Playing music")
                .setContentText("Dead Combo")
                .setContentIntent(pendingIntent)
                .setSmallIcon(android.R.drawable.star_on);

        Intent playIntent = new Intent(getApplicationContext(), MyService.class);
        playIntent.setAction(ACTION_PLAY);
        PendingIntent playPending = PendingIntent.getService(this, 0,
                playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(
                android.R.drawable.ic_media_play,"PLAY", playPending);
        builder.addAction(playAction);

        Intent pauseIntent = new Intent(getApplicationContext(), MyService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(getApplicationContext(), 0,
                pauseIntent, 0);
        NotificationCompat.Action pauseAction = new NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "PAUSE", pausePending);
        builder.addAction(pauseAction);

        Intent stopIntent = new Intent(this, MyService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPending = PendingIntent.getService(this, 0,
                stopIntent, 0);
        NotificationCompat.Action stopAction = new NotificationCompat.Action(
                android.R.drawable.ic_delete,"STOP", stopPending);
        builder.addAction(stopAction);

        Notification notification = builder.build();
        startForeground(1, notification);
        mp.start();
    }
}
