package com.example.vishnu.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private CameraManager cameraManager;
    private String cameraId;
    private ImageView imageView;
    private MediaPlayer mediaPlayer;
    private Boolean isFlashOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        isFlashOn = false;

        /**
         * Check device has Flash
         * no means exit screen
         */
        Boolean isFlashAvail = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvail) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle(getString(R.string.app_name));
            alert.setMessage(getString(R.string.error_msg));
            alert.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.exit), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int exit) {
                    finish();
                }
            });
            alert.show();
            return;
        }


        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isFlashOn) {
                        turnOffFlash();
                        isFlashOn = false;
                    } else {
                        turnOnFlash();
                        isFlashOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Method for Flash ON
     */
    public void turnOnFlash() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, true);
                playSound();
                imageView.setImageResource(R.drawable.on);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for Flash OFF
     */
    public void turnOffFlash() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, false);
                playSound();
                imageView.setImageResource(R.drawable.off);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playSound() {
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.flash_sound);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFlashOn) {
            turnOffFlash();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFlashOn) {
            turnOffFlash();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFlashOn) {
            turnOnFlash();
        }
    }
}
