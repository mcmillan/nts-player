package com.ntsradio.streaming;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

import java.io.IOException;

class PlaybackManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String currentUrl;
    private ReactApplicationContext reactApplicationContext;
    private WifiManager.WifiLock wifiLock;

    public PlaybackManager(ReactApplicationContext reactApplicationContext) {
        this.reactApplicationContext = reactApplicationContext;

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void play(String url) {
        if (url.equals(currentUrl)) {
            return;
        }

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
        } catch(IOException e) {
            mediaPlayer.reset();
            releaseWifiLock();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        acquireWifiLock();
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
}
