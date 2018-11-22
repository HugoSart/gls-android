package com.hugovs.gunfirelocatorstreamer.streamer;

public interface AudioStreamerListener {
    void onThrow(Exception e);
    void onStart();
    void onStop();
}
