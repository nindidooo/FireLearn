package com.example.android.firelearn;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button mRecordBtn;
    private TextView mRecordLabel;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private static final String LOG_TAG = "Record_log";

    private StorageReference mStorage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecordBtn = (Button) findViewById(R.id.RecordBtn);
        mRecordLabel = (TextView) findViewById(R.id.RecordLabel);
        mProgress = new ProgressDialog(this);

        mStorage = FirebaseStorage.getInstance().getReference();
        // Set location here
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Set file type here and THREE_GPP below
        mFileName += "/recorded_audio.3gp"; // that's the format we'll use, can be changed

        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    // user has pressed down on the button
                    startRecording();
                    mRecordLabel.setText("Recording Started ...");
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    // user has let go of the button
                    stopRecording();
                    mRecordLabel.setText("Recording Stopped ...");
                }
                return false;
            }
        });
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // change this for file
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio() {

        mProgress.setMessage("Uploading Audio ...");
        mProgress.show();
        StorageReference filepath = mStorage.child("Audio").child("new_audio.3gp");
        // create a uri file form filename string
        Uri uri = Uri.fromFile(new File(mFileName));

        // THIS PART AUTO-GENERATES
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
                mRecordLabel.setText("Uploading Finished");

            }
        });
    }

}
