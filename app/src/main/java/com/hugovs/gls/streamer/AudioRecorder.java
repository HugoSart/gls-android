package com.hugovs.gls.streamer;

import android.media.AudioRecord;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class records the audio from the main device's audio input.
 * The samples are written to the {@link OutputStream} and can be obtained in the {@link InputStream}.
 */
public class AudioRecorder {

    // Properties
    private final int minBuffSize;

    // References
    private AudioRecordThread task;
    private AudioRecord record;

    private final Collection<Listener> listeners = new ArrayList<>();

    public AudioRecorder(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
                         int bufferSizeInBytes) throws IllegalArgumentException {
        // Set constants
        this.minBuffSize = bufferSizeInBytes;

        // Initialize record
        this.record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 10);

    }

    /**
     * Starts the audio recording.
     */
    public void startRecording() {
        Log.d("GLS", "Starting audio recorder ...");
        record.startRecording();
        task = new AudioRecordThread(record);
        task.listeners = listeners;
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
     * Add the specified {@link Listener} to the listeners.
     * @param listener the {@link Listener} to be added.
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Remove the specified {@link Listener} from the listeners.
     * @param listener the {@link Listener} to be removed.
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Listener interface to act as callback on record data.
     */
    public interface Listener {
        void onDataReceived(byte[] data);
    }

    /**
     * Class that encapsulates the concurrent audio recording functionality.
     */
    private static class AudioRecordThread extends Thread {

        private AudioRecord record;
        private Collection<Listener> listeners = new ArrayList<>();

        AudioRecordThread(AudioRecord record) {
            this.record = record;
        }

        public void addListener(Listener listener) {
            listeners.add(listener);
        }

        public void removeListener(Listener listener) {
            listeners.remove(listener);
        }

        @Override
        public void run() {

            // Records and write to the buffer until the thread be interrupted.
            while (!isInterrupted() && record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                byte[] b = new byte[1280];
                record.read(b, 0, b.length);

                for (Listener listener : listeners)
                    listener.onDataReceived(b);

            }

            Log.d("GLS","Stop recording.");

        }

    }

}
