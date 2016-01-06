package net.beepinc.vip.MyAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.R;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.task.MyPostCustomList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by tayo on 9/19/2015.
 */
public class mypost_adapters extends RecyclerView.Adapter<mypost_adapters.MyPostAdapter> {

    Context context;
    private ArrayList<mypost_information> posts = new ArrayList<>();
    LayoutInflater inflater;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private UserLocalStore userLocalStore;
    private String imageUrl = AppConfig.web_url+"images/";
    private LongClickListener lc;
    private mClickListener clickListener;
    private RecyclerView recyclerView;
    private String what_to_do;

    public mypost_adapters(Context context,LongClickListener lc,mClickListener clickListener,RecyclerView recyclerView,String what_to_do) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        userLocalStore = new UserLocalStore(context);
        this.lc = lc;
        this.clickListener = clickListener;
        this.recyclerView = recyclerView;
        this.what_to_do = what_to_do;
    }

    public void setList(ArrayList<mypost_information> setPosts) {
        this.posts = setPosts;
        notifyDataSetChanged();
    }

    @Override
    public MyPostAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_myposts_adapter, parent, false);
        MyPostAdapter myPostAdapter = new MyPostAdapter(view);
        return myPostAdapter;
    }

    @Override
    public void onBindViewHolder(final MyPostAdapter holder, int position) {

        mypost_information current = posts.get(position);
        if(current.display.contentEquals("show")) {

            User user = userLocalStore.getLoggedUser();
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-images");
            File file = new File(path, current.image);
            if (current.caption.contains("VIP")) {
                holder.caption.setText(current.username);
            } else {
                holder.caption.setText(current.caption);
            }
            if (current.Response_icon != null)
                holder.response.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), setResponse(current.Response_icon)));

            if (file.exists()) {
                try {
                    holder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
                } catch (Exception e) {
                    Log.d("Image Error", e.toString());
                }
            } else {
                String getRealImageName = Uri.encode(current.image);
                imageLoader.get(imageUrl + getRealImageName, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.imageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        holder.imageView.setImageResource(R.drawable.avatar_default);
                    }
                });
            }
            Date getTime = new Date(current.time);
            long milli = getTime.getTime();

            holder.time.setText(net.beepinc.vip.json.TimeUtils.setAgo(milli));
            holder.duration.setText(SetMedia(current.voicenote));
        }

    }

    public int setResponse(String response) {
        int drawable = 0;
        if (response.contentEquals("timer_icon")) {
            drawable = R.drawable.timer_icon;
        } else {
            drawable = R.drawable.done_icon;
        }
        return drawable;
    }
//set void as string
    public String SetMedia(String vn) {

        MediaPlayer mediaPlayer = new MediaPlayer();
        double finalTime;
        String duration = "";

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-voicenotes/");
        File file = new File(path, vn);
        try {
            mediaPlayer.setDataSource(file.toString());
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
                TimeUnit.MILLISECONDS.toMinutes((long) finalT),// - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalT)),
                TimeUnit.MILLISECONDS.toSeconds((long) finalT) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalT)));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public interface LongClickListener{
        public void onLongClickListener(View view,int position,String instruct);
    }

    public interface mClickListener{
        public void onClickListener(View view,int position);
    }


    class MyPostAdapter extends RecyclerView.ViewHolder {

        Handler handler;
        ImageView imageView;
        ImageView response;
        TextView caption;
        TextView time;
        TextView duration;
        Button button;
        ProgressBar progressBar;
        MediaPlayer mediaPlayer;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-voicenotes/");
        Context context;
        int OnlyTime = 0;
        double startTime, finalTime;


        public MyPostAdapter(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.custom_mypost_dp);
            duration = (TextView) itemView.findViewById(R.id.custom_duration);
            response = (ImageView) itemView.findViewById(R.id.response);
            caption = (TextView) itemView.findViewById(R.id.custom_mypost_text);
            time = (TextView) itemView.findViewById(R.id.custom_mypost_time);
            button = (Button) itemView.findViewById(R.id.press);
            progressBar = (ProgressBar) itemView.findViewById(R.id.custom_mypost_subtext);
            context = itemView.getContext();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = button.getTag().toString();
                    final mypost_information current = posts.get(getPosition());
                    final File file = new File(path, current.voicenote);
                    String play_music = file.toString();

                    if(!file.exists()){
                        Toast.makeText(context,"file does not exist on sdcard. checking web...",Toast.LENGTH_LONG).show();
                        play_music = AppConfig.web_url+"voicenotes/"+current.voicenote;
                    }

                    if (text.contentEquals("play")) {
                        button.setTag("pause");
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.stop_icon);
                        Drawable d = new BitmapDrawable(bitmap);
                        button.setBackground(d);
                        handler = new Handler();
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(play_music);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            startTime = mediaPlayer.getCurrentPosition();
                            finalTime = mediaPlayer.getDuration();
                            if(OnlyTime == 0){
                                progressBar.setMax((int)finalTime);
                                OnlyTime = 1;
                            }
                            progressBar.setProgress((int)startTime);
                            handler.postDelayed(UpdateDuration, 100);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (text.contentEquals("pause")) {
                        button.setTag("play");
                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_icon);
                        Drawable d = new BitmapDrawable(bitmap);
                        button.setBackground(d);
                        mediaPlayer.pause();
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                    }


                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){
                        clickListener.onClickListener(v,getPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(lc != null) {
                        if(what_to_do == "post") {
                            lc.onLongClickListener(v, getPosition(), "post");
                        }else if(what_to_do == "fav")  {
                            lc.onLongClickListener(v, getPosition(), "fav");
                        }
                    }
                    return true;
                }
            });
        }

        private void setBackground(String tag){
            if(tag.contentEquals("play")){
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_icon);
                Drawable d = new BitmapDrawable(bitmap);
                button.setBackground(d);
            }else{
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.stop_icon);
                Drawable d = new BitmapDrawable(bitmap);
                button.setBackground(d);
            }
        }

        private Runnable UpdateDuration = new Runnable() {
            @Override
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                String currentPosition = String.format("%d m, %d s",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)));
                duration.setText(currentPosition);
                progressBar.setProgress((int)startTime);
                handler.postDelayed(this, 100);
                if(!mediaPlayer.isPlaying()) {
                    button.setTag("play");
                    progressBar.setMax(0);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_icon);
                    Drawable d = new BitmapDrawable(bitmap);
                    button.setBackground(d);
                }
            }
        };
    }
}

