package com.hugovs.gunfirelocatorstreamer.streamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class AudioStreamerClient {

    // Default
    private DatagramSocket socket;
    private AudioRecord recorder;

    // Audio properties
    private int minBufSize;
    private final int sampleRate;
    private final int channelConfig;
    private final int audioFormat;

    // Model
    private boolean status;

    public AudioStreamerClient() {
        this(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public AudioStreamerClient(int sampleRate, int channelConfig, int audioFormat) {
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    }

    public void start(final InetAddress ipAddress, final int port) {

        if (port < 0 || port > 65535) throw new AssertionError("Invalid port");
        Log.d("GLS", "Starting GLS Android Client ...");
        status = true;

        try {

            socket = new DatagramSocket();
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize * 10);
            recorder.startRecording();

            byte[] buffer = new byte[minBufSize];

            Log.d("GLS", "GLS Android Client stared!");

            while (status) {
                Log.d("GLS", "Sending packet ...");
                minBufSize = recorder.read(buffer, 0, buffer.length);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ipAddress, port);
                socket.send(packet);
                Log.d("GLS", "Packet sent!");
            }

            recorder.stop();

        } catch (Exception e) {
            Log.e("GLS", e.toString());
        }


    }

    public void stop() {
        Log.d("GLS", "Stopping GLS Android Client ...");
        status = false;
        Log.d("GLS", "GLS Android Client stopped!");
    }

    public boolean isStreaming() {
        return status;
    }

}
