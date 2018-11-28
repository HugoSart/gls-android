package com.hugovs.gls.streamer;

import android.media.AudioRecord;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class AudioRecorder {

    private AudioRecordThread task;
    private AudioRecord record;
    private Queue<byte[]> records;
    private int minBuffSize;
    private int recordBufferMaxSize;

    public AudioRecorder(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes,
                         int recordBufferMaxSize) throws IllegalArgumentException {
        minBuffSize = bufferSizeInBytes;
        record = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes * 10);
        records = new LinkedList<>();
        minBuffSize = bufferSizeInBytes;
        this.recordBufferMaxSize = recordBufferMaxSize;
    }

    public void startRecording() {
        Log.d("GLS", "Starting audio recorder ...");
        record.startRecording();
        task = new AudioRecordThread(record, records, minBuffSize, recordBufferMaxSize);
        task.start();
        Log.d("GLS","Audio recorder started!");
    }

    public void stopRecording() {
        record.release();
        task.interrupt();
    }

    public boolean isRecording() {
        return task != null && record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    public Queue<byte[]> getRecords() {
        return records;
    }

    private static class AudioRecordThread extends Thread {

        private AudioRecord record;
        private Queue<byte[]> records;
        private int minBuffSize, maxQueueSize;

        AudioRecordThread(AudioRecord record, Queue<byte[]> records, int minBuffSize, int maxQueueSize) {
            this.record = record;
            this.records = records;
            this.minBuffSize = minBuffSize;
            this.maxQueueSize = maxQueueSize;
        }

        @Override
        public void run() {

            while (!isInterrupted() && record.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                byte[] b = new byte[minBuffSize];
                minBuffSize = record.read(b, 0, b.length);
                records.add(b);
            }

            Log.d("GLS","Stop recording.");

        }

    }

}
