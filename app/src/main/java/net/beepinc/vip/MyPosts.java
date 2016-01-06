package net.beepinc.vip;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyAdapters.mypost_adapters;
import net.beepinc.vip.callback.MyPostsLoadedListener;
import net.beepinc.vip.task.MyPostCustomList;
import net.beepinc.vip.task.TaskLoadMyPosts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by tayo on 9/15/2015.
 */
public class MyPosts extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyPostsLoadedListener, mypost_adapters.LongClickListener, mypost_adapters.mClickListener {

    RecyclerView recyclerView;
    mypost_adapters adapters;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tv;
    ArrayList<mypost_information> customList = new ArrayList<>();
    private UserLocalStore userLocalStore;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.myposts, container, false);

        recyclerView = (RecyclerView)row.findViewById(R.id.mypostView);
        tv = (TextView)row.findViewById(R.id.my_post_tv);
        swipeRefreshLayout = (SwipeRefreshLayout)row.findViewById(R.id.swipe);
        adapters = new mypost_adapters(getActivity(),this,this,recyclerView,"post");
        userLocalStore = new UserLocalStore(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapters);

        loadPosts();


        swipeRefreshLayout.setOnRefreshListener(this);
        return row;
    }

    public void loadPosts(){
        User user = userLocalStore.getLoggedUser();
        customList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts(user.mob);
        if(!customList.isEmpty()){
            tv.setVisibility(View.GONE);
        }
        adapters.setList(customList);
    }

    public  void Ref(ArrayList<mypost_information> mycustomList){
        if (mycustomList != null) {
            //mycustomList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts();
            adapters.setList(mycustomList);
            //recyclerView.scrollToPosition(customList.size());
        }else {
            //customList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts();
            adapters.setList(customList);
        }
    }

    @Override
    public void onRefresh() {
        User user = userLocalStore.getLoggedUser();
        customList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts(user.mob);
        if(!customList.isEmpty()){
            tv.setVisibility(View.GONE);
        }
        adapters.setList(customList);
        recyclerView.scrollToPosition(customList.size());
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public void onMyPostsLoadedListener(ArrayList<mypost_information> list) {
//        if(!list.isEmpty()){
//            tv.setVisibility(View.GONE);
//        }
        adapters.setList(list);
    }

    @Override
    public void onLongClickListener(View view, int position,String instruct) {
        if(instruct == "post") {
            final mypost_information current = customList.get(position);
            final AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            ad.setTitle("Delete Alert");
            ad.setMessage("Are you sure you want to remove this voicenote?");
            ad.setButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MyApplication.getWriteableDatabaseForMyPosts().deleteDatabase(current.get_id);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onClickListener(View view, int position) {
        mypost_information current = customList.get(position);
        Bitmap bmd = BitmapFactory.decodeResource(getResources(), R.drawable.timer_icon);
        Drawable d = new BitmapDrawable(bmd);
        String response = current.Response_icon;

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-voicenotes/");
        File file = new File(path,current.voicenote);
        if(file.exists()) {
            String dur = VoiceDuration(file.toString());
            if (response.contentEquals("timer_icon")) {
                Toast.makeText(getActivity(), "retrying", Toast.LENGTH_LONG).show();
                MyPostCustomList postCustomList = new MyPostCustomList(getActivity(), current.caption, current.voicenote, current.image, current.mobile, current.username, current.time, file.toString(), "full",dur);
                postCustomList.doUploadFileForRetry(current.get_id,recyclerView);
                try {
                    loadPosts();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            Toast.makeText(getActivity(), "voicenote does not exist on sdcard", Toast.LENGTH_LONG).show();
        }
        loadPosts();
    }
}
