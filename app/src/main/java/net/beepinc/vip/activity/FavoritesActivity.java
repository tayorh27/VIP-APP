package net.beepinc.vip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyAdapters.mypost_adapters;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.R;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavoritesActivity extends ActionBarActivity implements mypost_adapters.LongClickListener {

    RecyclerView recyclerView;
    mypost_adapters adapters;
    TextView tv;
    ArrayList<mypost_information> customList = new ArrayList<>();
    private UserLocalStore userLocalStore;
    Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.mypostView);
        tv = (TextView)findViewById(R.id.my_post_tv);
        adapters = new mypost_adapters(FavoritesActivity.this,this,null,recyclerView,"fav");
        userLocalStore = new UserLocalStore(FavoritesActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));
        recyclerView.setAdapter(adapters);

        loadPosts();
    }

    public void loadPosts(){
        //try {
            User user = userLocalStore.getLoggedUser();
            customList = MyApplication.getWritableDatabaseForFavorites().getAllMyFavorites(user.mob);
            if (!customList.isEmpty()) {
                tv.setVisibility(View.GONE);
            }
            adapters.setList(customList);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private String VoiceDuration(String vn){
        MediaPlayer mediaPlayer = new MediaPlayer();
        double finalTime;
        String duration = "";
        try {
            mediaPlayer.setDataSource(vn);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            finalTime = mediaPlayer.getDuration();
            duration = ReturnDuration(finalTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    public String ReturnDuration(double finalT) {
        return String.format("%d m, %d s",
                TimeUnit.MILLISECONDS.toMinutes((long) finalT),
                TimeUnit.MILLISECONDS.toSeconds((long) finalT) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalT)));
    }

    @Override
    public void onLongClickListener(View view, int position, String instruct) {
        if(instruct == "fav") {
            final mypost_information current = customList.get(position);
            final AlertDialog ad = new AlertDialog.Builder(FavoritesActivity.this).create();
            ad.setTitle("Delete Alert");
            ad.setMessage("Are you sure you want to remove from favorites list?");
            ad.setButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyApplication.getWritableDatabaseForFavorites().deleteDatabase(current.get_id);
                    ad.dismiss();
                    loadPosts();
                }
            });
            ad.setButton2("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ad.dismiss();
                }
            });
            ad.show();
        }
    }
}
