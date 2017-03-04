package com.ntsradio.streaming;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.IOException;

class PlaybackManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private static PlaybackManager instance = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String currentUrl;
    private String status;
    private ReactApplicationContext reactApplicationContext;
    private WifiManager.WifiLock wifiLock;

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
    }

    protected void setContext(ReactApplicationContext context) {
        reactApplicationContext = context;
    }

    public void play(String url) {
        currentUrl = url;

        if (mediaPlayer != null) {
            mediaPlayer.reset();
            releaseWifiLock();
        } else {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.setWakeMode(this.reactApplicationContext, PowerManager.PARTIAL_WAKE_LOCK);

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            sendStatus("buffering");
        } catch(IOException e) {
            mediaPlayer.reset();
            releaseWifiLock();
        }
    }

    public void stop() {
        mediaPlayer.reset();
        releaseWifiLock();
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
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.release();
        releaseWifiLock();
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
}
