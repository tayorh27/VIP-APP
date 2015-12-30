package net.beepinc.vip.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.ImagePreview;
import net.beepinc.vip.Information.comment_information;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.InternetChecking;
import net.beepinc.vip.MyAdapters.comment_adapters;
import net.beepinc.vip.R;
import net.beepinc.vip.Social.Comments;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.callback.CommentsLoadedLisener;
import net.beepinc.vip.json.Utils;
import net.beepinc.vip.taskComment.TaskLoadComments;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentActivity extends ActionBarActivity implements View.OnClickListener, CommentsLoadedLisener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar, progressBar1;
    private TextView textView;
    private EditText editText;
    private Button button;

    private Comments comments;
    private UserLocalStore userLocalStore;
    private String post,username,caption,image,date,duration;

    ArrayList<comment_information> customList = new ArrayList<>();

    private comment_adapters adapters;

    InternetChecking isConn;
    boolean icheck = false;
    ActionBar actionBar;
    private String STATE_KEY = "comments";
    ImageView imageView;
    ImageView imageView2;
    TextView gCaption,gDate,gDur;
    Button gButton;
    ProgressBar gProgressBar;

    private MediaPlayer mediaPlayer;
    private Handler handler;
    int OnlyTime = 0;
    double startTime, finalTime;
    private Utils utils;
    SharedPreferences save;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        AppConfig.ReplaceDefaultFont(CommentActivity.this, "DEFAULT", "avenir_light.ttf");
        save = getSharedPreferences("saveEditText",0);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        utils = new Utils(CommentActivity.this);

        imageView = (ImageView) findViewById(R.id.custom_recentpost_dp);
        imageView2 = (ImageView) findViewById(R.id.download);
        gCaption = (TextView) findViewById(R.id.custom_recentpost_text);
        gDate = (TextView) findViewById(R.id.custom_recentpost_time);
        gDur = (TextView) findViewById(R.id.custom_duration);
        gButton = (Button) findViewById(R.id.recent_press);
        gProgressBar = (ProgressBar) findViewById(R.id.custom_recentpost_subtext);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        recyclerView = (RecyclerView)findViewById(R.id.comment_View);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar1 = (ProgressBar)findViewById(R.id.progressBar1);
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.comment_text);
        button = (Button)findViewById(R.id.btn_comment_send);

        adapters = new comment_adapters(CommentActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));
        recyclerView.setAdapter(adapters);

        userLocalStore = new UserLocalStore(CommentActivity.this);

        button.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        imageView.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        isConn = new InternetChecking(CommentActivity.this);

        icheck = isConn.isConnectedToInternet();
        if (!icheck) {
            Toast.makeText(CommentActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            textView.setText("Oooop no internet");
        }

        //setUp();

        Bundle bundle = getIntent().getExtras();
        post = bundle.getString("voicenote");
        username = bundle.getString("username");
        caption = bundle.getString("caption");
        image = bundle.getString("image");
        date = bundle.getString("date");
        duration = bundle.getString("duration");

        actionBar.setTitle("Comments - "+username);
        gCaption.setText(caption);
        gDate.setText(date);
        gDur.setText(duration);
        gButton.setOnClickListener(this);
        try {
            if(savedInstanceState != null){
                progressBar.setVisibility(View.GONE);
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
                editText.setText(savedInstanceState.getString("ct"));
            }else {
                if(customList.isEmpty()) {
                    new TaskLoadComments(this, post).execute();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            if(!save.getString(post,"").contentEquals("")){
                editText.setText(save.getString(post,""));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        adapters.setList(customList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        String text = editText.getText().toString();
        if(!text.contentEquals("")){
            SharedPreferences.Editor editMe = save.edit();
            editMe.putString(post,text);
            editMe.apply();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(CommentActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_comment_send:
                sendComment();
                break;
            case R.id.recent_press:
                PlayMedia();
                break;
            case R.id.download:
                utils.DownloadVoiceToSDcard(post);
                break;
            case R.id.custom_recentpost_dp:
                displayDP();
                break;
        }
    }

    private void displayDP() {
        String web_url = AppConfig.web_url+"images/";
        BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bmd.getBitmap();
        String url = image;
        Bundle bundle = new Bundle();
        bundle.putString("image", web_url+ Uri.encode(url));
        bundle.putString("username", username);
        Intent intent = new Intent(CommentActivity.this, ImagePreview.class);
        intent.putExtras(bundle);
        if (bitmap != null) {
            startActivity(intent);
        } else {
            Toast.makeText(CommentActivity.this, "No Image yet", Toast.LENGTH_LONG).show();
        }
    }

    private void PlayMedia() {
        String text = gButton.getTag().toString();
        if (text.contentEquals("play")) {
            gButton.setTag("pause");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stop_icon);
            Drawable d = new BitmapDrawable(bitmap);
            gButton.setBackground(d);
            handler = new Handler();
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(getMediaPath(post));
                mediaPlayer.prepare();
                mediaPlayer.start();
                startTime = mediaPlayer.getCurrentPosition();
                finalTime = mediaPlayer.getDuration();
                if (OnlyTime == 0) {
                    gProgressBar.setMax((int) finalTime);
                    OnlyTime = 1;
                }
                gProgressBar.setProgress((int) startTime);
                handler.postDelayed(UpdateDuration, 100);


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (text.contentEquals("pause")) {
            gButton.setTag("play");
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_icon);
            Drawable d = new BitmapDrawable(bitmap);
            gButton.setBackground(d);
            mediaPlayer.pause();
            gProgressBar.setProgress(mediaPlayer.getCurrentPosition());
        }
    }

    private String getMediaPath(String post) {
        String get_path = "";
        String web_url = AppConfig.web_url+"voicenotes/" + post;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-recent-voicenotes/");
        File file = new File(path, post);
        if (file.exists()) {
            get_path = file.toString();
        } else {
            get_path = web_url;
        }
        return get_path;
    }

    Runnable UpdateDuration = new Runnable() {
        @Override
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            String currentPosition = String.format("%d m, %d s",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)));
            //duration.setText(currentPosition);
            gProgressBar.setProgress((int) startTime);
            handler.postDelayed(this, 100);
            if (!mediaPlayer.isPlaying()){//!mediaPlayer.isPlaying()) {
                gButton.setTag("play");
                gProgressBar.setMax(0);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.play_icon);
                Drawable d = new BitmapDrawable(bitmap);
                gButton.setBackground(d);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String comment_text = editText.getText().toString();
        outState.putParcelableArrayList(STATE_KEY, customList);
        outState.putString("ct", comment_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void sendComment() {
        String txt = editText.getText().toString();
        if(!txt.contentEquals("")) {
            progressBar1.setVisibility(View.VISIBLE);
            User user = userLocalStore.getLoggedUser();
            String comment = editText.getText().toString();
            comments = new Comments(CommentActivity.this, editText, progressBar1, post, user.image, user.mob, user.uname, comment);
            comments.startTask();
            SharedPreferences.Editor editor = save.edit();
            if(!save.getString(post,"").contentEquals("")) {
                editor.remove(post);
            }
            new TaskLoadComments(this, post).execute();
        }

    }

    @Override
    public void onCommentsLoadedLisener(ArrayList<comment_information> lists) {
        adapters.setList(lists);
        //Toast.makeText(CommentActivity.this, "size = "+lists.size(), Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
        if (lists.isEmpty()){
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        customList.clear();
        customList = lists;
    }

    @Override
    public void onRefresh() {
        icheck = isConn.isConnectedToInternet();
        if (icheck) {
            new TaskLoadComments(this,post).execute();
        } else {
            //Toast.makeText(CommentActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            textView.setVisibility(View.VISIBLE);
            textView.setText("Oooop no internet");
            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }
}
