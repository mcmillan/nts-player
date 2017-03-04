package com.ntsradio.streaming;


import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class StreamingModule extends ReactContextBaseJavaModule {
    private PlaybackManager playbackManager;

    public StreamingModule(ReactApplicationContext context) {
        super(context);
        playbackManager = PlaybackManager.getInstance(context);
    }

    @Override
    public String getName() {
        return "Streaming";
    }

    @ReactMethod
    public void play(String url) {
        playbackManager.play(url);
    }

    @ReactMethod
    public void stop() {
        playbackManager.stop();
    }

    @ReactMethod
    public void refresh() {
        playbackManager.refresh();
    }
}
