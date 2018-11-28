package com.hugovs.gls.streamer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.hugovs.gls.util.asynctask.AsyncTaskListener;
import com.hugovs.gls.util.asynctask.ObservableAsyncTask;

import java.net.InetAddress;

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

    public void setListener(AsyncTaskListener<Void, Void, Void> listener) {
        this.listener = listener;
    }

    public void startAudioStreaming(final InetAddress ipAddress, final int port) {

        if (port < 0 || port > 65535) throw new AssertionError("Invalid port");
        Log.d("GLS", "Starting GLS Android Client ...");

        audioRecorder = new AudioRecorder(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, minBufSize, 128000);
        audioStreamer = new AudioStreamer(audioRecorder.getRecords(), ipAddress, port);

        task = new AudioStreamingTask(audioRecorder, audioStreamer);
        if (listener != null) task.setListener(listener);
        task.execute();

        Log.d("GLS", "GLS Android Client started!");

    }

    public void stopAudioStreaming() {
        task.cancel(false);
    }

    public boolean isRecording() {
        return audioRecorder != null && audioRecorder.isRecording();
    }

    public boolean isStreaming(){
        return audioStreamer != null && audioStreamer.isStreaming();
    }

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
            onPostExecute(null);
            return null;
        }

        @Override
        public void onPostExecute(Void o) {
            listener.onPostExecute(o);
        }
    }



}
