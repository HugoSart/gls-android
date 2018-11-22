package com.hugovs.gunfirelocatorstreamer.activity;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hugovs.gunfirelocatorstreamer.R;
import com.hugovs.gunfirelocatorstreamer.streamer.AudioStreamer;
import com.hugovs.gunfirelocatorstreamer.streamer.AudioStreamerListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    // Views
    private Button btConnect;
    private EditText etIP, etPort;
    private LinearLayout llConnection;
    private TextView tvConnectionStatus, tvConnectionElapsedTime;

    private Thread timeCounterThread;
    private AudioStreamer streamer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        btConnect = findViewById(R.id.btConnect);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        llConnection = findViewById(R.id.llConnection);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        tvConnectionElapsedTime = findViewById(R.id.tvConnectionElapsedTime);

        timeCounterThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 0;
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        tvConnectionStatus.setText(String.valueOf(time++) + "s");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        streamer = new AudioStreamer();

        llConnection.setVisibility(streamer.isStreaming() ? View.VISIBLE : View.INVISIBLE);

        streamer.addListener(new AudioStreamerListener() {
            @Override
            public void onThrow(Exception e) {
                Log.d("Exception", "Error in stream", e);
                llConnection.setVisibility(View.INVISIBLE);
                etIP.setEnabled(true);
                etPort.setEnabled(true);
                btConnect.setText("Conectar");
            }

            @Override
            public void onStart() {
                llConnection.setVisibility(View.VISIBLE);
                etIP.setEnabled(false);
                etPort.setEnabled(false);
                btConnect.setText("Desconectar");
                tvConnectionStatus.setText("Online");
                timeCounterThread.start();
            }

            @Override
            public void onStop() {
                llConnection.setVisibility(View.INVISIBLE);
                etIP.setEnabled(true);
                etPort.setEnabled(true);
                btConnect.setText("Conectar");
                timeCounterThread.stop();
            }
        });

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    streamer.start(InetAddress.getByName(etIP.getText().toString()), Short.valueOf(etPort.getText().toString()));
                } catch (UnknownHostException e) {
                    Log.d("Exception", "Invalid ip address", e);
                }
            }
        });

    }

}
