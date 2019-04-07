package com.hugovs.gls.streamer;

import android.media.AudioRecord;
import android.util.Log;

import com.hugovs.gls.core.AudioData;
import com.hugovs.gls.core.AudioInput;
import com.hugovs.gls.core.util.ByteUtils;

import java.util.Calendar;

/**
 * An {@link AudioInput} that reads the audio samples from an Android audio source.
 *
 * @author Hugo Sartori
 */
public class RecorderInput implements AudioInput {

    private AudioRecord record;
    private int bufferSizeInBytes;

    /**
     * Crate an instance of this class.
     *
     * @param audioSource: the audio source to read the samples.
     * @param sampleRateInHz: the sample rate.
     * @param channelConfig: the channel configuration.
     * @param audioFormat: the audio format.
     * @param bufferSizeInBytes: the buffer size to store the samples.
     */
    public RecorderInput(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        this.bufferSizeInBytes = bufferSizeInBytes;
        this.record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 10);
    }

    /**
     * Called when the server is started.
     * Start the audio recording.
     */
    @Override
    public void open() {
        record.startRecording();
    }

    /**
     * Called when the server is closed.
     * Closes the record source.
     */
    @Override
    public void close() {
        record.release();
    }

    /**
     * Read a {@link AudioData} from the defined Android audio source.
     *
     * @return the read {@link AudioData}.
     */
    @Override
    public AudioData read() {
        if (record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            byte[] b = new byte[bufferSizeInBytes + 16];
            record.read(b, 16, b.length - 16);

            long timestamp = Calendar.getInstance().getTimeInMillis();
            byte[] timestampBytes = ByteUtils.longToBytes(timestamp);
            System.arraycopy(timestampBytes, 0, b, 8, 8);

            return AudioData.wrap(b);
        }

        Log.d("GLS", "Audio readed : null");
        return null;
    }

}
