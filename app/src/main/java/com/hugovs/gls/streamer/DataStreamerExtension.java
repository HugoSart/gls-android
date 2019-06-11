package com.hugovs.gls.streamer;

import android.util.Log;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioListener;
import com.hugovs.gls.core.AudioServerExtension;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * This extension send the received data using udp packets.
 *
 * @author Hugo Sartori
 */
public class DataStreamerExtension extends AudioServerExtension implements AudioListener {

    private final DatagramSocket socket;
    private final DatagramPacket packet;

    /**
     * Create an instance of this class.
     *
     * @param ip: the ip to send the packets.
     * @param port: the port to send the packets.
     */
    public DataStreamerExtension(InetAddress ip, int port) {

        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new IllegalArgumentException(e);
        }

        packet = new DatagramPacket(new byte[0], 0, 0, ip, port);

    }

    /**
     * Called when a new data is received from the input line.
     *
     * @param audioData: {@link AudioData} from the input line or {@code null} if there is none;
     */
    @Override
    public void onDataReceived(AudioData audioData) {
        try {
            // Log.d("GLS","Sending packet: " + StringUtils.from(audioData.getSamples()));
            packet.setData(AudioData.unwrap(audioData));
            socket.send(packet);
        } catch (IOException e) {
            Log.e("GLS", "Failed to send packet", e);
        }
    }

}
