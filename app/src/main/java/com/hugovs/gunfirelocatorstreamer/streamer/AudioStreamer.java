package com.hugovs.gunfirelocatorstreamer.streamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Set;

public class AudioStreamer {

    // Default
    private DatagramSocket socket;
    private AudioRecord recorder;
    private Thread thread;

    // Listeners
    private Set<AudioStreamerListener> listeners;

    // Audio properties
    private final int minBufSize;
    private final int sampleRate;
    private final int channelConfig;
    private final int audioFormat;

    // Model
    private boolean status;

    public AudioStreamer() {
        this(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public AudioStreamer(int sampleRate, int channelConfig, int audioFormat) {
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat) + 2048;
    }

    public void start(final InetAddress ipAddress, final int port) {

        if (port < 0 || port > 65535) throw new AssertionError("Invalid port");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new DatagramSocket();
                    byte[] buffer = new byte[minBufSize];
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
                    recorder.startRecording();

                    byte[] sendData = new byte[1024];
                    byte[] receiveData = new byte[1024];

                    for (AudioStreamerListener listener : listeners) listener.onStart();

                    while (status && socket.isConnected()) {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
                        socket.send(sendPacket);
                    }
                } catch (Exception e) {
                    for (AudioStreamerListener listener : listeners) listener.onThrow(e);
                }
            }
        });

        thread.start();
    }

    public void stop() {
        socket.close();
        for (AudioStreamerListener listener : listeners) listener.onStop();
    }

    public boolean isStreaming() {
        return status;
    }

    public void addListener(AudioStreamerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AudioStreamerListener listener) {
        listeners.remove(listener);
    }

}
