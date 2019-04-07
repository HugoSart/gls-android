package com.hugovs.gls.streamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.hugovs.gls.core.AudioServer;

import java.net.InetAddress;

/**
 * An {@link AudioServer} Factory to create instances for this application.
 *
 * @author Hugo Sartori
 */
public class AudioServerFactory {

    private AudioServerFactory() {
        //no instance
    }

    /**
     * Create an {@link AudioServer} with a {@link DataStreamerExtension} and {@link RecorderInput}.
     * This is a setup to a audio streamer using the device's microphone.
     *
     * @param address: the address to send the stream data.
     * @param port: the port to send the stream data.
     * @param sampleRateInHz: the sample rate to process the data.
     * @return the instance of {@link AudioServer} for stream.
     */
    public static AudioServer createAudioStreamer(InetAddress address, int port, int sampleRateInHz) {
        AudioServer audioServer = new AudioServer(sampleRateInHz, 16);
        audioServer.setInput(new RecorderInput(
                MediaRecorder.AudioSource.MIC,
                sampleRateInHz,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)));
        audioServer.addExtension(new DataStreamerExtension(address, port));
        return audioServer;
    }

}
