package com.ntsradio.streaming;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.ntsradio.MainActivity;
import com.ntsradio.R;


import java.io.IOException;


class PlaybackManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
    private static PlaybackManager instance = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String currentUrl;
    private String status;
    private ReactApplicationContext reactApplicationContext;
    private WifiManager.WifiLock wifiLock;
    private Notification playbackNotification;
    private NotificationManager notificationManager;
    private String notificationTitle;
    private String notificationText;

    public static PlaybackManager getInstance(ReactApplicationContext context) {
        if (instance == null) {
            instance = new PlaybackManager();
        }
        instance.setContext(context);
        return instance;
    }

    protected PlaybackManager() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
    }

    protected void setContext(ReactApplicationContext context) {
        reactApplicationContext = context;
    }

    public void setNotificationTitle(String title) {
        notificationTitle = title;
    }

    public void setNotificationText(String text) {
        notificationText = text;
    }

    public void play(String url) {
        currentUrl = url;

        if (mediaPlayer != null) {
            reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.setWakeMode(this.reactApplicationContext, PowerManager.PARTIAL_WAKE_LOCK);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            sendStatus("buffering");
        } catch(IOException e) {
            reset();
        }
    }

    public void stop() {
        reset();
        sendStatus("stopped");
    }

    public void refresh() {
        sendStatus(null);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        acquireWifiLock();
        sendStatus("playing");
        sendNotification();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        reset();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        log("got some info: " + what);
        switch(what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                sendStatus("buffering");
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                sendStatus("playing");
                break;
            case 703: // MEDIA_INFO_NETWORK_BANDWIDTH
                log("network bandwidth: " + extra);
                break;
        }

        return true;
    }



    private void log(String line) {
        Log.v("NTS", line);
    }

    private void acquireWifiLock() {
        if (wifiLock == null) {
            wifiLock = ((WifiManager) reactApplicationContext.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "nts");
        }

        if (!wifiLock.isHeld()) {
            wifiLock.acquire();
        }
    }

    private void releaseWifiLock() {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    private void sendStatus(String status) {
        if (status == null) {
            status = this.status;
        } else {
            this.status = status;
        }
        log("sending status: " + status);
        WritableMap options = Arguments.createMap();
        options.putString("playStatus", status);
        options.putString("currentUrl", currentUrl);
        reactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("playbackStatusUpdate", options);
    }

    private void reset() {
        mediaPlayer.reset();
        releaseWifiLock();
        removeNotification();
    }

    public void sendNotification() {
        if (!status.equals("playing")) {
            return;
        }

        Intent notificationIntent = new Intent(reactApplicationContext, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(reactApplicationContext, 0, notificationIntent, 0);

        playbackNotification = new NotificationCompat.Builder(reactApplicationContext)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle("Now Playing: " + notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(intent)
                .setAutoCancel(false)
                .setOngoing(true)
                .setStyle(new NotificationCompat.MediaStyle())
                .build();


        getNotificationManager().notify(1, playbackNotification);
    }

    private void removeNotification() {
        getNotificationManager().cancel(1);
    }

    private NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager)reactApplicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return notificationManager;
    }
}
