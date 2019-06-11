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
    private long deviceId;

    // Aux
    private byte[] deviceIdBytes;

    /**
     * Crate an instance of this class.
     *
     * @param audioSource: the audio source to read the samples.
     * @param sampleRateInHz: the sample rate.
     * @param channelConfig: the channel configuration.
     * @param audioFormat: the audio format.
     * @param bufferSizeInBytes: the buffer size to store the samples.
     */
    public RecorderInput(long deviceId, int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        this.deviceId = deviceId;
        this.bufferSizeInBytes = bufferSizeInBytes;
        this.record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 10);

        deviceIdBytes = ByteUtils.longToBytes(deviceId);
        Log.d("GLS", "Id: [" + deviceIdBytes[0] + ", "+ deviceIdBytes[1] + ", "+ deviceIdBytes[2] + ", "+ deviceIdBytes[3] + ", "+ deviceIdBytes[4] + ", "+ deviceIdBytes[5] + ", "+ deviceIdBytes[6] + ", "+ deviceIdBytes[7] + "]");
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
            record.read(b, 16, bufferSizeInBytes);

            // Copy device's id
            deviceIdBytes = ByteUtils.longToBytes(deviceId);
            System.arraycopy(deviceIdBytes, 0, b, 0, 8);

            // Copy timestamp
            long timestamp = Calendar.getInstance().getTimeInMillis();
            byte[] timestampBytes = ByteUtils.longToBytes(timestamp);
            System.arraycopy(timestampBytes, 0, b, 8, 8);

            return AudioData.wrap(b);
        }

        Log.d("GLS", "Audio readed : null");
        return null;
    }

}
