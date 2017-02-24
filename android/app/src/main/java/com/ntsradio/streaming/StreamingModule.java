package com.ntsradio.streaming;


import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


public class StreamingModule extends ReactContextBaseJavaModule {
    private PlaybackManager playbackManager;

    public StreamingModule(ReactApplicationContext context) {
        super(context);
        playbackManager = new PlaybackManager(context);
    }

    @Override
    public String getName() {
        return "Streaming";
    }

    @ReactMethod
    public void play(String url) {
        playbackManager.play(url);
    }
}
