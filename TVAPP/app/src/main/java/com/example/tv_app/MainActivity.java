package com.example.tv_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {


    ProgressDialog mDialog;
    VideoView videoView;

    String ipAddressString;

    EditText ipAddressEditText;
    Button saveButton;
    LinearLayout mLayout;

     String videoURL;
   //String videoURL = "http://192.168.1.113/test/video/video.mp4";
    // Global SharePref Const Variables
    public static final String SHARED_PREFS = "sharePrefs";
    public static final String TEXT = "text";

    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        videoView = (VideoView) findViewById(R.id.video_view);
        mLayout = (LinearLayout)findViewById(R.id.server_linear_layout);

        // SharedPreferences use to first time App Run
        SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = sp.getBoolean("firstStart", true);

        if (firstStart) {
            showAlertDialog();
            mLayout.setVisibility(LinearLayout.VISIBLE);
        }
//         Load Ip Address into SharedPreferences
        loadIpAddress();

        VideoStreaming();
        // Auto Reload Video View
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(40000); // every 30s
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Video Streaming method
                                VideoStreaming();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

    }


    private void loadIpAddress() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        ipAddressString = sp.getString(TEXT, "");
    }

    private void showAlertDialog() {

        ipAddressEditText = (EditText) findViewById(R.id.editText_ip_address);
        saveButton = (Button) findViewById(R.id.button_save);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipAddressString = ipAddressEditText.getText().toString();
                saveIPAddress();
                mLayout.setVisibility(LinearLayout.INVISIBLE);
                VideoStreaming();
            }
        });

        // Set IP AlertDialog One time For First Time App
        SharedPreferences sp = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void saveIPAddress() {
        SharedPreferences sp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(TEXT, ipAddressString);

        editor.apply();
        Toast.makeText(MainActivity.this, "Saved IP Address " + ipAddressString, Toast.LENGTH_LONG).show();
    }

    private void VideoStreaming() {

       videoURL = "http://"+ipAddressString+"/test/video/video.mp4";
        // Video View Functionality
        try {
            Uri uri = Uri.parse(videoURL);
            videoView.setVideoURI(uri);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            ex.printStackTrace();
        }

        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // mDialog.dismiss();
                mp.setLooping(true);
                videoView.start();
            }
        });

    }
}
