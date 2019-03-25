package com.hugovs.gls.activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.hugovs.gls.R;
import com.hugovs.gls.streamer.AudioStreamerClient;
import com.hugovs.gls.util.asynctask.BaseAsyncTaskListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends Activity {

    // Views
    private Button btConnect;
    private EditText etIP, etPort, etSampleRate;

    private AudioStreamerClient streamer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("GLS-Android", MODE_PRIVATE);

        // Custom code
        checkPermissions();
        initViews();
        setupAudioStreamerClient();

    }

    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        }

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 123);
        }
    }

    private void initViews() {
        btConnect = findViewById(R.id.btConnect);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        etSampleRate = findViewById(R.id.etSampleRate);
        etIP.setText(sharedPreferences.getString("ip", "192.168.0.8"));
        etPort.setText(sharedPreferences.getString("port", "55555"));
        etSampleRate.setText(sharedPreferences.getString("sample_rate", "16000"));
    }

    private void setupAudioStreamerClient() {
        streamer = new AudioStreamerClient(Integer.valueOf(etSampleRate.getText().toString()));
        streamer.setListener(new BaseAsyncTaskListener<Void, Void, Void>() {

            @Override
            public void onPreExecute() {
                setupConnectionViews(true);
            }

            @Override
            public void onPostExecute(Void o) {
                setupConnectionViews(false);
            }

            @Override
            public void onCancelled() {
                setupConnectionViews(false);
            }

        });

        btConnect.setOnClickListener(v -> {
            if (sharedPreferences != null)
                sharedPreferences.edit()
                        .putString("ip", etIP.getText().toString())
                        .putString("port", etPort.getText().toString())
                        .putString("sample_rate", etSampleRate.getText().toString())
                        .apply();

            if (!streamer.isRecording()) {
                try {
                    streamer.setSampleRate(Integer.valueOf(etSampleRate.getText().toString()));
                    streamer.start(InetAddress.getByName(etIP.getText().toString()), Integer.valueOf(etPort.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                streamer.stop();
                setupConnectionViews(false);
            }
        });

    }

    private void setupConnectionViews(boolean connected) {
        etIP.setEnabled(!connected);
        etPort.setEnabled(!connected);
        etSampleRate.setEnabled(!connected);
        btConnect.setText(connected ? getString(R.string.disconnect) : getString(R.string.connect));
    }

}
