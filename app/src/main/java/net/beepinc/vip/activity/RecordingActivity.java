package net.beepinc.vip.activity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.Async.UploadVoice;
import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyAdapters.mypost_adapters;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.MyPosts;
import net.beepinc.vip.R;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.generalUsage.Uploadings;
import net.beepinc.vip.json.Utils;
import net.beepinc.vip.task.MyPostCustomList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class RecordingActivity extends ActionBarActivity {

    Button speak;
    private EditText caption;
    FloatingActionButton fab;
    FloatingActionButton fab1;
    TextView displayText;

    private MediaRecorder mediaRecorder;
    private String outputPath;
    private boolean isRecording = false;
    private boolean checkRecord = false;
    private String Filename;

    UserLocalStore userLocalStore;

    private Uploadings uploadings;

    private UploadVoice uploadVoice;
    private mypost_adapters adapter;
    private MyPostCustomList myPostCustomList;
    private Utils utils;
    private Handler handler;
    private int counter = 0;
    private String length_of_record="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        //getSupportActionBar().hide();
        utils = new Utils();
        handler = new Handler();

        speak = (Button) findViewById(R.id.btn_speak);
        caption = (EditText) findViewById(R.id.caption);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_cancel);
        displayText = (TextView) findViewById(R.id.display);

        userLocalStore = new UserLocalStore(RecordingActivity.this);
        uploadings = new Uploadings();
        displayText.setVisibility(View.GONE);
        adapter = new mypost_adapters(this);

        if (!hasMicrophone()) {
            speak.setEnabled(false);
            Toast.makeText(RecordingActivity.this, "Your mobile phone does not support MICROPHONE", Toast.LENGTH_LONG).show();
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-voicenotes/");
        Random random = new Random();
        Calendar c = Calendar.getInstance();
        User user = userLocalStore.getLoggedUser();
        Filename = user.uname + "_voicenote_" + c.get(Calendar.YEAR) + "_" + c.get(Calendar.MONTH) + "_" + c.get(Calendar.DATE) + "_" + random.nextInt(123454) + ".3gp";
        File file = new File(path, Filename);
        path.mkdirs();

        outputPath = file.toString();

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordingActivity.this, "Long press to start recording - " + outputPath, Toast.LENGTH_LONG).show();
            }
        });

        speak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mic2);
                    Drawable d = new BitmapDrawable(bitmap);
                    speak.setBackground(d);
                    startRecord();

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mic1);
                    Drawable d = new BitmapDrawable(bitmap);
                    speak.setBackground(d);
                    stopRecord();
                }
                return true;
            }
        });
        speak.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRecord)
                    upload_to_server();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void upload_to_server() {
        User user = userLocalStore.getLoggedUser();
        Random random = new Random();
        String setCaption = caption.getText().toString();
        if (setCaption.contentEquals("")) {
            setCaption = "VIP"+random.nextInt(12345872);
        }
        final String foreignKey = user.mob;
        final String filename = Filename;
        final String getImage = user.image;
        final String getUser = user.uname;

        Date date = new Date();
        String currentDate = date.toLocaleString();

        setList(setCaption, filename, getImage, foreignKey, getUser, "timer_icon", currentDate);
        myPostCustomList = new MyPostCustomList(RecordingActivity.this,setCaption, filename, getImage, foreignKey, getUser, currentDate, outputPath,"partial");
        myPostCustomList.startTask();


        //ArrayList<mypost_information> mycustomList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts();
        //serverUpload(setCaption, filename, getImage, foreignKey, getUser);

        //after_upload(setCaption);
        finish();

    }

    private void after_upload(String setCaption) {
        final String finalSetCaption = setCaption;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int response = uploadings.uploadFile(RecordingActivity.this,outputPath);
                if(response == 200){
                    int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
                    //setList(finalSetCaption, filename, getImage, foreignKey, getUser, "done_icon");
                    MyApplication.getWriteableDatabaseForMyPosts().updateDatabase(fk, "done_icon");
                    //new MyPosts().Ref();
                }else{
                    //setList(finalSetCaption, filename, getImage, foreignKey, getUser, "timer_icon");
                }
            }
        }).start();
    }

    private void setList(String caption, String voicenote, String img, String mob, String username, String bitmap, String cTime){

        ArrayList<mypost_information> customData = new ArrayList<>();
        int fk = MyApplication.getWriteableDatabaseForMyPosts().getLastId();
        int id = fk+1;
        mypost_information current = new mypost_information(id,caption, voicenote, img, mob, username, bitmap, cTime,"partial");
        customData.add(current);

        MyApplication.getWriteableDatabaseForMyPosts().insertMyPost(customData, false);
    }

    private void serverUpload(String caption, String voicenote, String img, String mob, String username){
        new UploadVoice(RecordingActivity.this,caption,voicenote,img,mob,username).execute();
        //Log.e("uploading", "Success to database");
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }

    private void startRecord() {
        if(utils.checkSDCard() == "allow" && utils.checkSDCARDSIZE() == "memory_available") {
            isRecording = true;
            checkRecord = true;
            displayText.setVisibility(View.VISIBLE);
            try {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(outputPath);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaRecorder.start();
            handler.postDelayed(recordValue,1000);
            //Toast.makeText(RecordingActivity.this, "Recording started", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(RecordingActivity.this, "No sdcard or sdcard available space is low", Toast.LENGTH_LONG).show();
        }

    }

    private void stopRecord() {
        if (isRecording) {
            length_of_record = "00:"+counter;
            counter = 0;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            displayText.setVisibility(View.GONE);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mic1);
            Drawable d = new BitmapDrawable(bitmap);
            speak.setBackground(d);
            Toast.makeText(RecordingActivity.this, "Recording stopped", Toast.LENGTH_LONG).show();
        }
    }

    private Runnable recordValue = new Runnable() {
        @Override
        public void run() {
            counter++;
            displayText.setText("Recording 00:"+counter);
            handler.postDelayed(this,1000);
            if(counter == 60){
                stopRecord();
            }
        }
    };

}
