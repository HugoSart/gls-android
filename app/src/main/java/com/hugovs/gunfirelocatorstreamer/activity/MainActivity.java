package com.hugovs.gunfirelocatorstreamer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hugovs.gunfirelocatorstreamer.R;
import com.hugovs.gunfirelocatorstreamer.streamer.AudioStreamerClient;
import com.hugovs.gunfirelocatorstreamer.util.ToastUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends Activity {

    // Views
    private Button btConnect;
    private EditText etIP, etPort;
    private LinearLayout llConnection;
    private TextView tvConnectionStatus, tvConnectionElapsedTime;

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
        llConnection = findViewById(R.id.llConnection);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        tvConnectionElapsedTime = findViewById(R.id.tvConnectionElapsedTime);
    }

    private void setupAudioStreamerClient() {
        streamer = new AudioStreamerClient();
        llConnection.setVisibility(streamer.isStreaming() ? View.VISIBLE : View.INVISIBLE);
        btConnect.setOnClickListener(v -> {
            if (!streamer.isStreaming()) new AudioStreamerClientTask(streamer).execute(etIP.getText().toString(), etPort.getText().toString());
            else streamer.stop();
        });

    }

    private void setupConnectionViews(boolean connected) {
        etIP.setEnabled(!connected);
        etPort.setEnabled(!connected);
        btConnect.setText(connected ? getString(R.string.disconnect) : getString(R.string.connect));
        tvConnectionStatus.setText(connected ? getString(R.string.online) : getString(R.string.offline));
        tvConnectionElapsedTime.setText("");
        llConnection.setVisibility(connected ? View.VISIBLE : View.INVISIBLE);
    }

    private class AudioStreamerClientTask extends AsyncTask<String, Void, Void> {

        private AudioStreamerClient client;

        public AudioStreamerClientTask(AudioStreamerClient client) {
            this.client = client;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                this.client.start(InetAddress.getByName(params[0]), Integer.valueOf(params[1]));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            setupConnectionViews(true);
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            setupConnectionViews(false);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            setupConnectionViews(false);
        }

        @Override
        protected void onCancelled() {
            setupConnectionViews(false);
        }
    }

}
