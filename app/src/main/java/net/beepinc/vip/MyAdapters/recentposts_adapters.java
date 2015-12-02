package net.beepinc.vip.MyAdapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import net.beepinc.vip.ImagePreview;
import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.R;
import net.beepinc.vip.Social.FetchComment;
import net.beepinc.vip.Social.FetchLike;
import net.beepinc.vip.Social.Likes;
import net.beepinc.vip.SocialAsyn.TaskLoadNumberOfComments;
import net.beepinc.vip.SocialAsyn.TaskLoadNumberOfLikes;
import net.beepinc.vip.SocialCallbacks.LoadNumberOfCommentsListener;
import net.beepinc.vip.SocialCallbacks.LoadNumberOfLikesListener;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.activity.CommentActivity;
import net.beepinc.vip.json.TimeUtils;
import net.beepinc.vip.json.Utils;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.task.MyPostCustomList;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by tayo on 9/19/2015.
 */
public class recentposts_adapters extends RecyclerView.Adapter<recentposts_adapters.RecentAdapter> implements LoadNumberOfCommentsListener, LoadNumberOfLikesListener {

    ArrayList<recent_post_information> informations;
    private LayoutInflater inflater;
    private Context context;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private UserLocalStore userLocalStore;
    //private TaskLoadNumberOfComments taskLoadNumberOfComments;
    private int nlikes;
    private int ncomments;
    private String last_duration="";

    public recentposts_adapters(Context context_) {
        this.context = context_;
        inflater = LayoutInflater.from(context);
        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        userLocalStore = new UserLocalStore(context);
    }

    public void setLists(ArrayList<recent_post_information> infos) {
        this.informations = infos;
        notifyDataSetChanged();
    }

    @Override
    public RecentAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_recentposts_adapter, parent, false);
        RecentAdapter recentAdapter = new RecentAdapter(view);
        return recentAdapter;
    }

    @Override
    public void onBindViewHolder(final RecentAdapter holder, int position) {
        final recent_post_information current = informations.get(position);

        if(current.uploaded.contentEquals("full")) {

            String web_url = "http://www.gisanrinadetayo.comuf.com/images/";
            User user = userLocalStore.getLoggedUser();

            //new TaskLoadNumberOfComments(this, current.voicenote).execute();
            holder.comments.setText(current.nComments + " comment(s)");
            //ncomments = 0;


            String check_like = current.nLikeUser;
            String[] nChecks = check_like.split(",");
            for(int i = 0; i < nChecks.length; i++){
                if(nChecks[i].contentEquals(current.username)){
                    holder.likes.setTextColor(context.getResources().getColor(R.color.like));
                }
            }

            //new TaskLoadNumberOfLikes(this, current.voicenote).execute();
            holder.likes.setText(current.nLikes + " like(s)");
            //nlikes = 0;

            holder.username.setText(current.username);
            if (current.caption.contains("VIP")) {
                holder.caption.setText("No Caption");
            } else {
                holder.caption.setText(current.caption);
            }

            Date getTime = new Date(current.time);
            long milli = getTime.getTime();
            holder.time.setText(TimeUtils.setAgo(milli));

            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-profile-pictures/");
            File file = new File(path, current.image);
            if (file.exists()) {
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
            } else {
                String getRealImageName = Uri.encode(current.image);
                imageLoader.get(web_url + getRealImageName, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        holder.imageView.setImageBitmap(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
            holder.duration.setText(SetMedia(current.voicenote));
        }


    }

    public String SetMedia(final String vn) {

        new AsyncTask<String,String,String> (){
            @Override
            protected String doInBackground(String... params) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                double finalTime;
                String duration = "";
                try {
                    mediaPlayer.setDataSource(getMediaPath(vn));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    finalTime = mediaPlayer.getDuration();
                    duration = ReturnDuration(finalTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return duration;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                last_duration = s;
            }
        }.execute();
        return last_duration;
    }

    public String ReturnDuration(double finalT) {
        return String.format("%d m, %d s",
                TimeUnit.MILLISECONDS.toMinutes((long) finalT),// - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalT)),
                TimeUnit.MILLISECONDS.toSeconds((long) finalT) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalT)));
    }

    private String getMediaPath(String vn) {
        String get_path = "";
        String web_url = "http://www.gisanrinadetayo.comuf.com/voicenotes/" + vn;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-recent-voicenotes/");
        File file = new File(path, vn);
        if (file.exists()) {
            get_path = file.toString();
        } else {
            get_path = web_url;
        }
        return get_path;
    }

    @Override
    public int getItemCount() {
        return informations.size();
    }

    @Override
    public void onLoadNumberOfCommentsListener(ArrayList<String> n) {
        ncomments = n.size();
    }

    @Override
    public void onLoadNumberOfLikesListener(ArrayList<String> n) {
        nlikes = n.size();
    }


    public class RecentAdapter extends RecyclerView.ViewHolder implements View.OnClickListener, LoadNumberOfLikesListener, LoadNumberOfCommentsListener {

        private Context context;
        private MediaPlayer mediaPlayer;
        private Handler handler;
        ImageView imageView;
        ImageView imageView2;
        TextView caption, time, duration, likes, comments,username,repost;
        Button button;
        ProgressBar progressBar;
        int OnlyTime = 0;
        double startTime, finalTime;
        private UserLocalStore userLocalStore;
        private Utils utils;

        public RecentAdapter(View itemView) {
            super(itemView);
            context = itemView.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.custom_recentpost_dp);
            imageView2 = (ImageView) itemView.findViewById(R.id.download);
            caption = (TextView) itemView.findViewById(R.id.custom_recentpost_text);
            time = (TextView) itemView.findViewById(R.id.custom_recentpost_time);
            duration = (TextView) itemView.findViewById(R.id.custom_duration);
            likes = (TextView) itemView.findViewById(R.id.custom_likes);
            comments = (TextView) itemView.findViewById(R.id.custom_comments);
            username = (TextView)itemView.findViewById(R.id.custom_name);
            repost = (TextView)itemView.findViewById(R.id.custom_reposted);
            button = (Button) itemView.findViewById(R.id.recent_press);
            progressBar = (ProgressBar) itemView.findViewById(R.id.custom_recentpost_subtext);
            userLocalStore = new UserLocalStore(context);
            utils = new Utils(context);

            imageView.setOnClickListener(this);
            imageView2.setOnClickListener(this);
            button.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            repost.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            User user = userLocalStore.getLoggedUser();
            switch (v.getId()) {
                case R.id.custom_recentpost_dp:
                    displayDP();
                    break;
                case R.id.recent_press:
                    PlayMedia();
                    break;
                case R.id.custom_likes:
                    //perform like
                    LikeClicked(user);
                    break;
                case R.id.custom_comments:
                    //perform comment
                    recent_post_information current1 = informations.get(getPosition());
                    OpenCommentActivity(current1.voicenote);
                    break;
                case R.id.download:
                    //perform download
                    recent_post_information current2 = informations.get(getPosition());
                    utils.DownloadVoiceToSDcard(current2.voicenote);
                    break;
                case R.id.custom_reposted:
                    RepostVN();
                    break;
            }
        }

        private void RepostVN() {
            recent_post_information current = informations.get(getPosition());
            Date date = new Date();
            String currentDate = date.toLocaleString();
            User user = userLocalStore.getLoggedUser();
            String username = user.uname+" shared "+current.username+"'s post";
            MyPostCustomList postCustomList = new MyPostCustomList(context,current.caption, current.voicenote, user.image, user.mob, username,currentDate,"","full");
            postCustomList.Upload_to_Database_repost();

        }

        private void LikeClicked(User user) {
            if (likes.getTextColors().getDefaultColor() != context.getResources().getColor(R.color.like)) {
                recent_post_information current = informations.get(getPosition());
                new Likes(context, likes, current.voicenote, user.image, user.mob, user.uname, "liked" + user.uname, "1").startTask();
                new TaskLoadNumberOfLikes(this,current.voicenote).execute();
                new TaskLoadNumberOfComments(this,current.voicenote).execute();
                //likes.setText(nlikes.size() + "like(s)");
            } else {
                Toast.makeText(context, "voicenote liked already!", Toast.LENGTH_LONG).show();
            }
        }

        private void OpenCommentActivity(String voicenote) {
            Bundle bundle = new Bundle();
            bundle.putString("voicenote", voicenote);
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }

        private String getMediaPath(String vn) {
            String get_path = "";
            String web_url = "http://www.gisanrinadetayo.comuf.com/voicenotes/" + vn;
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC + "/vip-recent-voicenotes/");
            File file = new File(path, vn);
            if (file.exists()) {
                get_path = file.toString();
            } else {
                get_path = web_url;
            }
            return get_path;
        }

        private void PlayMedia() {
            String text = button.getTag().toString();
            recent_post_information current = informations.get(getPosition());

            if (text.contentEquals("play")) {
                button.setTag("pause");
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.stop_icon);
                Drawable d = new BitmapDrawable(bitmap);
                button.setBackground(d);
                handler = new Handler();
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getMediaPath(current.voicenote));
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    startTime = mediaPlayer.getCurrentPosition();
                    finalTime = mediaPlayer.getDuration();
                    if (OnlyTime == 0) {
                        progressBar.setMax((int) finalTime);
                        OnlyTime = 1;
                    }
                    progressBar.setProgress((int) startTime);
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
            }
        }

        Runnable UpdateDuration = new Runnable() {
            @Override
            public void run() {
                startTime = mediaPlayer.getCurrentPosition();
                String currentPosition = String.format("%d m, %d s",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)));
                duration.setText(currentPosition);
                progressBar.setProgress((int) startTime);
                handler.postDelayed(this, 100);
                if (!mediaPlayer.isPlaying()) {
                    button.setTag("play");
                    progressBar.setMax(0);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_icon);
                    Drawable d = new BitmapDrawable(bitmap);
                    button.setBackground(d);
                }
            }
        };

        private void displayDP() {
            recent_post_information current = informations.get(getPosition());
            String web_url = "http://www.gisanrinadetayo.comuf.com/images/";
            BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bmd.getBitmap();
            String url = current.image;
            String username = current.username;
            Bundle bundle = new Bundle();
            bundle.putString("image", web_url+Uri.encode(url));
            bundle.putString("username", username);
            Intent intent = new Intent(context, ImagePreview.class);
            intent.putExtras(bundle);
            if (bitmap != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "No Image yet", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onLoadNumberOfLikesListener(ArrayList<String> n) {
            likes.setText(n.size() +" like(s)");
        }

        @Override
        public void onLoadNumberOfCommentsListener(ArrayList<String> n) {
            comments.setText(n.size() +" comment(s)");
        }
    }
}
