package com.hugovs.gls.streamer;

import android.media.AudioRecord;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * This class records the audio from the main device's audio input.
 * The samples are written to the {@link OutputStream} and can be obtained in the {@link InputStream}.
 *
 * @apiNote
 * Use the {@linkplain #getInputStream()} method to read the samples in real-time.
 */
class AudioRecorder {

    // Properties
    private final int minBuffSize;

    // Streams
    private final PipedOutputStream outputStream;
    private final PipedInputStream inputStream;

    // References
    private AudioRecordThread task;
    private AudioRecord record;

    public AudioRecorder(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
                         int bufferSizeInBytes) throws IllegalArgumentException, IOException {
        // Set constants
        this.minBuffSize = bufferSizeInBytes;

        // Initialize Streams
        this.outputStream = new PipedOutputStream();
        this.inputStream = new PipedInputStream();
        outputStream.connect(inputStream);

        // Initialize record
        this.record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 10);

    }

    /**
     * Starts the audio recording.
     */
    public void startRecording() {
        Log.d("GLS", "Starting audio recorder ...");
        record.startRecording();
        task = new AudioRecordThread(record, outputStream, minBuffSize);
        task.start();
        Log.d("GLS","Audio recorder started!");
    }

    /**
     * Stops the audio recording.
     */
    public void stopRecording() {
        record.release();
        task.interrupt();
    }

    /**
     * Checks if the audio is being recorded.
     * @return  {@code true} if the audio is being recorded;
     *          {@code false} if not.
     */
    public boolean isRecording() {
        return task != null && record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    /**
     * Returns the audio samples {@link InputStream}.
     * @return the audio samples {@link InputStream}.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Class that encapsulates the concurrent audio recording functionality.
     */
    private static class AudioRecordThread extends Thread {

        private AudioRecord record;
        private OutputStream outputStream;
        private int minBuffSize;

        AudioRecordThread(AudioRecord record, OutputStream outputStream, int minBuffSize) {
            this.record = record;
            this.outputStream = outputStream;
            this.minBuffSize = minBuffSize;
        }

        @Override
        public void run() {

            // Records and write to the buffer until the thread be interrupted.
            while (!isInterrupted() && record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                byte[] b = new byte[1028];
                record.read(b, 0, b.length);

                try {
                    outputStream.write(b);
                } catch (IOException e) {
                    Log.e("GLS", "Failed to write to output stream");
                }

            }

            Log.d("GLS","Stop recording.");

        }

    }

}
