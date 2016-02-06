package net.beepinc.vip.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.ImagePreview;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.MyAdapters.recentposts_adapters;
import net.beepinc.vip.R;
import net.beepinc.vip.callback.RecentPostsLoadedListener;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.searchResult.TaskLoadSearchResultList;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserActivity extends ActionBarActivity implements RecentPostsLoadedListener {


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    Toolbar toolbar;
    ImageView pic;
    TextView username,cat,progress_text;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    recentposts_adapters adapters;

    String name,category,image;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    String web_url = AppConfig.web_url+"images/";
    ArrayList<recent_post_information> customList = new ArrayList<>();
    private String STATE_KEY = "user_posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        pic = (ImageView)findViewById(R.id.custom_recentpost_dp);
        username = (TextView)findViewById(R.id.custom_recentpost_text);
        cat = (TextView)findViewById(R.id.custom_recentpost_time);
        recyclerView = (RecyclerView)findViewById(R.id.post_View);
        progress_text = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        adapters = new recentposts_adapters(UserActivity.this,recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(UserActivity.this));
        recyclerView.setAdapter(adapters);

        try {
            if(savedInstanceState != null){
                progressBar.setVisibility(View.GONE);
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            }else {
                if(customList.isEmpty()) {
                    new TaskLoadSearchResultList(this,name).execute();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        adapters.setLists(customList);

        try {
            Bundle bundle = getIntent().getExtras();
            name = bundle.getString("uname");
            category = bundle.getString("ucategory");
            image = bundle.getString("uimage");

            getSupportActionBar().setTitle(name.toUpperCase());
            cat.setText(category);
            username.setText("0 post");
//web_url+
            String imageUrl =  Uri.encode(image);

            Toast.makeText(UserActivity.this,"image = "+image+"\nurl = "+imageUrl,Toast.LENGTH_LONG).show();

            imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    pic.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    GlobalUse.handleVolleyError(volleyError,UserActivity.this);
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDP();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, customList);
    }

    private void displayDP() {
        BitmapDrawable bmd = (BitmapDrawable) pic.getDrawable();
        Bitmap bitmap = bmd.getBitmap();
        String url = image;
        String username = name;
        Bundle bundle = new Bundle();
        bundle.putString("image", Uri.encode(url));
        bundle.putString("username", username);
        Intent intent = new Intent(UserActivity.this, ImagePreview.class);
        intent.putExtras(bundle);
        if (bitmap != null) {
            UserActivity.this.startActivity(intent);
        } else {
            Toast.makeText(UserActivity.this, "No Image yet", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRecentPostsLoadedListener(ArrayList<recent_post_information> lists) {
        adapters.setLists(lists);
        progressBar.setVisibility(View.GONE);
        if (lists.isEmpty()){
            progress_text.setVisibility(View.VISIBLE);
        }else {
            progress_text.setVisibility(View.GONE);
            int size = lists.size();
            if(size == 1){
                username.setText(size+" post");
            }else{
                username.setText(size+" posts");
            }
        }customList.clear();
        customList = lists;
    }
}
