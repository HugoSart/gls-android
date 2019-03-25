package com.hugovs.gls.streamer.listener;

import android.util.Log;

import com.hugovs.gls.streamer.AudioRecorder;
import com.hugovs.gls.util.ByteUtils;
import com.hugovs.gls.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

/**
 * This class is the one who sends new stream data to the remote server.
 */
public class DataStreamer implements AudioRecorder.Listener {

    private final DatagramSocket socket;
    private final InetAddress ipAddress;
    private final int port;
    private final DatagramPacket packet;

    public DataStreamer(InetAddress ip, int port) {
        this.ipAddress = ip;
        this.port = port;

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalArgumentException(e);
        }

        packet = new DatagramPacket(new byte[0], 0, 0, ip, port);

    }

    @Override
    public void onDataReceived(byte[] data) {
        // Get current timestamp

        try {
            packet.setData(data);
            socket.send(packet);
        } catch (IOException e) {
            Log.e("GLS", "Failed to send packet", e);
        }
    }

}
