package com.vsb.kru13.osmzhttpserver;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends Activity {

    private static final int WRITE_REQUEST_CODE = PackageManager.PERMISSION_GRANTED;
    private Camera mCamera;
    private CameraPreview mPreview;
    public byte[] cameraData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        checkCameraHardware(getApplicationContext());

        Log.d("CAMERATAG", "InsideOfOnCreate");

        mCamera = getCameraInstance();

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        Button captureButton = (Button) findViewById(R.id.button_capture);

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                        Log.d("CameraShot", "shot");

                        //send data to html


                    }
                }
        );
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

            Log.d("CAMERATAG","accessing the kamera");
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.d("CAMERATAG", "exception thrown while accessing camera instance.");
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("CAMERA", "ONPICTURETAKEN");

            CameraData.cemeraPictureData = data;

            camera.release();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    private File getOutputMediaFile() {

        File mediaStorageDir = new File(
                String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "testImage.jpg");

        return mediaFile;
    }
}
