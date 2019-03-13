package com.hugovs.gls.streamer;

import android.util.Log;

import com.hugovs.gls.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * This class is the one who sends new stream data to the remote server.
 */
class AudioStreamer {

    private final DatagramSocket socket;
    private final InetAddress ipAddress;
    private final int port;
    private final int minBuffSize;

    // Streams
    private final InputStream stream;

    // Thread
    private AudioStreamThread task;

    public AudioStreamer(InputStream stream, InetAddress ip, int port, int minBuffSize) {
        this.stream = stream;
        this.ipAddress = ip;
        this.port = port;
        this.minBuffSize = minBuffSize;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalArgumentException(e);
        }

    }

    /**
     * Starts the streaming.
     */
    public void startStreaming() {
        Log.d("GLS", "Starting audio streamer ...");
        task = new AudioStreamThread(socket, stream, ipAddress, port, minBuffSize);
        task.start();
        Log.d("GLS", "Audio streamer started!");
    }

    /**
     * Stops the streaming.
     */
    public void stopStreaming() {
        task.interrupt();
    }

    /**
     * Checks if the streaming is happening.
     * @return  {@code true} if it's streaming;
     *          {@code false} if not.
     */
    public boolean isStreaming() {
        return task != null && task.isAlive();
    }

    /**
     * This thread is capable of send stream bytes remotely and concurrently.
     */
    private static class AudioStreamThread extends Thread {

        private DatagramSocket socket;
        private InputStream stream;
        private InetAddress ip;
        private int port;
        private int minBuffSize;

        AudioStreamThread(DatagramSocket socket, InputStream stream, InetAddress ip, int port, int minBuffSize) {
            this.socket = socket;
            this.stream = stream;
            this.ip = ip;
            this.port = port;
            this.minBuffSize = minBuffSize;
        }

        @Override
        public void run() {

            Log.d("GLS", "Streaming ...");
            byte[] buffer = new byte[minBuffSize];

            // Run while the thread is not interrupted
            while (!interrupted()) {
                try {
                    int read = minBuffSize;

                    // Sends the new stream data
                    if ((read = stream.read(buffer, 0, read)) > 0) {
                        Log.d("GLS", "Sending " + buffer.length + " bytes: " + StringUtils.from(buffer));
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, port);
                        socket.send(packet);
                    }

                } catch (IOException e) {
                    Log.e("GLS", "Failed to read stream: ", e);
                }
            }

            Log.d("GLS", "Stop streaming.");

        }

    }




}
