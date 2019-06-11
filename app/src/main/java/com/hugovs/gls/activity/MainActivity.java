package com.hugovs.gls.activity;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hugovs.gls.R;
import com.hugovs.gls.core.AudioServer;
import com.hugovs.gls.streamer.AudioServerFactory;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * GLS Android App main activity.
 *
 * @author Hugo Sartori
 */
public class MainActivity extends Activity {

    // Views
    private Button btConnect;
    private TextView tvIdentifierValue;
    private EditText etIP, etPort, etSampleRate;

    // References
    private AudioServer audioServer;
    private SharedPreferences sharedPreferences;

    // Aux
    private long deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("GLS-Android", MODE_PRIVATE);

        // Custom code
        deviceId = new BigInteger(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID), 16).longValue();
        Log.d("GLS", "Device's id: " + deviceId);

        checkPermissions();
        initViews();
        setupConnectButton();

    }

    /**
     * Setup the connect button to create or destroy the audio server.
     */
    private void setupConnectButton() {
        btConnect.setOnClickListener(v -> {
            if (sharedPreferences != null)
                sharedPreferences.edit()
                        .putString("ip", etIP.getText().toString())
                        .putString("port", etPort.getText().toString())
                        .putString("sample_rate", etSampleRate.getText().toString())
                        .apply();

            // Start server or close if is running
            if (audioServer == null) {
                // Open the audio server
                try {
                    audioServer = AudioServerFactory.createAudioStreamer(InetAddress.getByName(etIP.getText().toString()), Integer.valueOf(etPort.getText().toString()), deviceId, Integer.valueOf(etSampleRate.getText().toString()));
                    audioServer.start();
                    setupConnectionViews(true);
                } catch (UnknownHostException e) {
                    Log.d("GLS", "Failed to open the audio streamer", e);
                }
            } else {
                // Close the audio server
                audioServer.close();
                audioServer = null;
                setupConnectionViews(false);
            }
        });
    }

    /**
     * Setup the views to match the server state.
     *
     * @param connected: if the views should be configured as
     *                   connected ({@code true}) or
     *                   disconnected ({@code false}).
     */
    private void setupConnectionViews(boolean connected) {
        etIP.setEnabled(!connected);
        etPort.setEnabled(!connected);
        etSampleRate.setEnabled(!connected);
        btConnect.setText(connected ? getString(R.string.disconnect) : getString(R.string.connect));
    }

    /**
     * Checks at runtime for all the permissions required to run the app.
     */
    private void checkPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 123);
        }

        if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 123);
        }
    }

    /**
     * Initialize the views references and setup the initial configuration.
     */
    private void initViews() {
        // Ref
        btConnect = findViewById(R.id.btConnect);
        tvIdentifierValue = findViewById(R.id.tvIdentifierValue);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        etSampleRate = findViewById(R.id.etSampleRate);

        // Setup
        tvIdentifierValue.setText(String.valueOf(deviceId));
        etIP.setText(sharedPreferences.getString("ip", "192.168.0.8"));
        etPort.setText(sharedPreferences.getString("port", "55555"));
        etSampleRate.setText(sharedPreferences.getString("sample_rate", "16000"));
    }

}
