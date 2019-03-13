package com.hugovs.gls.streamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.hugovs.gls.util.asynctask.AsyncTaskListener;
import com.hugovs.gls.util.asynctask.ObservableAsyncTask;

import java.io.IOException;
import java.net.InetAddress;

/**
 * This class implements the Facade pattern for the streamer construction.
 */
public class AudioStreamerClient {

    private AudioStreamingTask task;
    private AudioRecorder audioRecorder;
    private AudioStreamer audioStreamer;

    private AsyncTaskListener<Void, Void, Void> listener;

    // Audio properties
    private int minBufSize;
    private final int sampleRate;
    private final int channelConfig;
    private final int audioFormat;

    public AudioStreamerClient() {
        this(16000);
    }

    public AudioStreamerClient(int sampleRate) {
        this(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
    }

    public AudioStreamerClient(int sampleRate, int channelConfig, int audioFormat) {
        this.sampleRate = sampleRate;
        this.channelConfig = channelConfig;
        this.audioFormat = audioFormat;
        this.minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        Log.d("GLS", "Minimum buffer size: " + minBufSize);
    }

    /**
     * Set the default listener.
     * @param listener the listener to serve as callback.
     */
    public void setListener(AsyncTaskListener<Void, Void, Void> listener) {
        this.listener = listener;
    }

    /**
     * Starts the audio streaming remotely.
     * @param ipAddress the remote address.
     * @param port the remote port.
     */
    public void start(final InetAddress ipAddress, final int port) throws IOException {

        if (port < 0 || port > 65535) throw new AssertionError("Invalid port");
        Log.d("GLS", "Starting GLS Android Client ...");

        audioRecorder = new AudioRecorder(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize);
        audioStreamer = new AudioStreamer(audioRecorder.getInputStream(), ipAddress, port, minBufSize);

        task = new AudioStreamingTask(audioRecorder, audioStreamer);
        if (listener != null) task.setListener(listener);
        task.execute();

        Log.d("GLS", "GLS Android Client started!");

    }

    /**
     * Stops the audio streaming.
     */
    public void stop() {
        task.cancel(false);
    }

    /**
     * Checks if this client is recording audio.
     * @return  {@code true} if this client is recording audio;
     *          {@code false} if not.
     */
    public boolean isRecording() {
        return audioRecorder != null && audioRecorder.isRecording();
    }

    /**
     * Checks if this client is streaming the samples.
     * @return  {@code true} if this client is streaming the samples;
     *          {@code false} if not.
     */
    public boolean isStreaming(){
        return audioStreamer != null && audioStreamer.isStreaming();
    }

    /**
     * This task is intended to effectively start the recorder and the streamer without blocking
     * the main UI thread.
     */
    private static class AudioStreamingTask extends ObservableAsyncTask<Void, Void, Void> {

        private final AudioRecorder recorder;
        private final AudioStreamer streamer;

        AudioStreamingTask(AudioRecorder recorder, AudioStreamer streamer) {
            this.recorder = recorder;
            this.streamer = streamer;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            recorder.startRecording();
            while (!recorder.isRecording());
            streamer.startStreaming();
            while (!isCancelled() && streamer.isStreaming());

            streamer.stopStreaming();
            recorder.stopRecording();
            return null;
        }

        @Override
        public void onPostExecute(Void o) {
            listener.onPostExecute(o);
        }
    }



}
