package com.example.videotrimmer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.example.videotrimmer.databinding.ActivityVideoshowBinding;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

public class VideoShow extends AppCompatActivity {
    Uri uri;

    String startTime;
    String endTime;
    ProgressDialog loadingBar;
    FFmpeg ffmpeg;
    File dest;
    String destfilePath;
    String original_path;
    ActivityVideoshowBinding activityVideoshowBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVideoshowBinding =  DataBindingUtil.setContentView(this,R.layout.activity_videoshow);
        loadingBar = new ProgressDialog(this);
        Intent in = getIntent();
        if(in!=null)
        {
            String imagePath=in.getStringExtra("uri");
            uri=Uri.parse(imagePath);
        }
        activityVideoshowBinding.videoView.setVideoURI(uri);
        activityVideoshowBinding.videoView.start();



        activityVideoshowBinding.compbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTime = activityVideoshowBinding.startTime.getText().toString();
                endTime = activityVideoshowBinding.endTime.getText().toString();
                if(TextUtils.isEmpty(startTime))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Start Time",Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(endTime))
                {
                    Toast.makeText(getApplicationContext(),"Please Enter End Time",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(checkPermission())
                    {
                        File folder = new File(Environment.getExternalStorageDirectory()+"/TrimmedVideos");
                        if(!folder.exists())
                        {
                            folder.mkdir();
                        }
                        getRealPathFromUri(getApplicationContext(),uri);
                        String filename=original_path.substring(original_path.lastIndexOf("/")+1);
                        dest = new File(folder,filename);
                        destfilePath=folder.getAbsolutePath()+"/"+filename;
                        Log.d("path", destfilePath);
                        String[] comm = {"-ss", "" + Integer.parseInt(startTime) , "-y", "-i", original_path, "-t", "" + (Integer.parseInt(endTime)-Integer.parseInt(startTime)),"-vcodec", "mpeg4", "-ac", "2", "-ar", "22050", destfilePath};
                        loadFFMpegBinary();
                        execFFMpegCommand(comm);
                    }
                    else
                    {
                        requestPermission();
                    }


                }
            }
        });


    }
    public void loadFFMpegBinary()
    {
        try {
            if(ffmpeg==null)
            {
                ffmpeg = FFmpeg.getInstance(this);
            }
            ffmpeg.loadBinary(new LoadBinaryResponseHandler(){
                @Override
                public void onFailure() {
                    super.onFailure();
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public  void execFFMpegCommand(String[] command)
    {
        try{

            ffmpeg.execute(command, new ExecuteBinaryResponseHandler()
            {
                @Override
                public void onFailure(String message) {
                    Log.d("res","Failed : "+message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.d("res","success : "+message);
                }

                @Override
                public void onProgress(String message) {
                    Log.d("res","progress : "+message);
                }

                @Override
                public void onStart() {
                    loadingBar.setMessage("Progressing");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    Log.d("res", "Started command : ffmpeg " + command);
                }

                @Override
                public void onFinish() {
                    Log.d("res","Finished command : ffmpeg "+ command);
                    loadingBar.dismiss();
                    Intent in = new Intent(VideoShow.this, VideoTrimmedActivity.class);
                    in.putExtra("comressedfilepath", destfilePath);
                    startActivity(in);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor=context.getContentResolver().query(contentUri,proj,null,null,null);
            int coulumn_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            original_path = cursor.getString(coulumn_index);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(cursor!=null)
            {
                cursor.close();
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(VideoShow.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(VideoShow.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(VideoShow.this, "Write External Storage permission allows us to create files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(VideoShow.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

}