package com.hugovs.gls.activity;

import android.Manifest;
import android.app.Activity;
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
    private EditText etIP, etPort;

    private AudioStreamerClient streamer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }

    private void setupAudioStreamerClient() {
        streamer = new AudioStreamerClient(16000);
        streamer.setListener(new BaseAsyncTaskListener<Void, Void, Void>() {

            @Override
            public void onPreExecute() {
                setupConnectionViews(true);
            }

            @Override
            public void onPostExecute(Void o) {
                setupConnectionViews(false);
                Log.d("GLS", "foi");
            }

            @Override
            public void onCancelled() {
                setupConnectionViews(false);
                Log.d("GLS", "foi");
            }

        });

        btConnect.setOnClickListener(v -> {
            if (!streamer.isRecording()) {
                try {
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
        btConnect.setText(connected ? getString(R.string.disconnect) : getString(R.string.connect));
    }

}
