package com.hugovs.gls.streamer.listener;

import android.util.Log;

import com.hugovs.gls.streamer.AudioRecorder;
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
public class DataStreamer implements AudioRecorder.Listener {

    private final DatagramSocket socket;
    private final InetAddress ipAddress;
    private final int port;

    public DataStreamer(InetAddress ip, int port) {
        this.ipAddress = ip;
        this.port = port;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalArgumentException(e);
        }

    }

    @Override
    public void onDataReceived(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            Log.e("GLS", "Failed to send packet", e);
        }
    }

}
