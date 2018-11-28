package com.hugovs.gls.streamer;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;

class AudioStreamer {

    private AudioStreamThread task;
    private DatagramSocket socket;
    private Queue<byte[]> records;
    private InetAddress ipAddress;
    private int port;

    public AudioStreamer(Queue<byte[]> records, InetAddress ip, int port) {
        this.records = records;
        this.ipAddress = ip;
        this.port = port;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public void startStreaming() {
        Log.d("GLS", "Starting audio streamer ...");
        task = new AudioStreamThread(socket, records, ipAddress, port);
        task.start();
        Log.d("GLS", "Audio streamer started!");
    }

    public void stopStreaming() {
        task.interrupt();
    }

    public boolean isStreaming() {
        return task != null && task.isAlive();
    }

    private static class AudioStreamThread extends Thread {

        private DatagramSocket socket;
        private Queue<byte[]> records;
        private InetAddress ip;
        private int port;

        AudioStreamThread(DatagramSocket socket, Queue<byte[]> records, InetAddress ip, int port) {
            this.socket = socket;
            this.records = records;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            Log.d("GLS", "Streaming ...");
            while (!interrupted()) {
                while (!records.isEmpty()) {
                    byte[] data = records.poll();
                    DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
                    try {
                        socket.send(packet);
                    } catch (IOException e) {
                        Log.e("GLS", "Failed to send packet");
                    }
                }
            }

            Log.d("GLS", "Stop streaming.");

        }

    }


}
