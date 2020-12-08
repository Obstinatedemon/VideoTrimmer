package com.example.videotrimmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.videotrimmer.databinding.ActivityVideoTrimmedBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class VideoTrimmedActivity extends AppCompatActivity {
    ActivityVideoTrimmedBinding activityVideoTrimmedBinding;

    String compressedFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVideoTrimmedBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_trimmed);
        Intent i = getIntent();
        if(i!=null)
        {
            compressedFilePath = i.getStringExtra("comressedfilepath");
        }
        activityVideoTrimmedBinding.trimmedVideoView.setVideoPath(compressedFilePath);
        activityVideoTrimmedBinding.trimmedVideoView.start();
        activityVideoTrimmedBinding.setPauplayButton("Pause");
        activityVideoTrimmedBinding.ppbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activityVideoTrimmedBinding.getPauplayButton().equals("Pause"))
                {
                    activityVideoTrimmedBinding.trimmedVideoView.pause();
                    activityVideoTrimmedBinding.setPauplayButton("Play");
                }
                else
                {
                    activityVideoTrimmedBinding.trimmedVideoView.start();
                    activityVideoTrimmedBinding.setPauplayButton("Pause");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(VideoTrimmedActivity.this,MainActivity.class);
        startActivity(setIntent);
    }
}