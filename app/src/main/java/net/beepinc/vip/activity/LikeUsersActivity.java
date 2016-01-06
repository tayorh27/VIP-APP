package net.beepinc.vip.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.beepinc.vip.MyAdapters.favorites_adapters;
import net.beepinc.vip.R;
import net.beepinc.vip.SocialExtras.FetchLikeUsers;

public class LikeUsersActivity extends ActionBarActivity {

    RecyclerView recyclerView;
    TextView tv;
    ProgressBar progressBar;
    favorites_adapters adapters;
    Toolbar toolbar;

    String vn="";
    FetchLikeUsers fetchLikeUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_users);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        tv = (TextView)findViewById(R.id.textView3);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        adapters = new favorites_adapters(LikeUsersActivity.this,"hide");
        recyclerView.setLayoutManager(new LinearLayoutManager(LikeUsersActivity.this));
        recyclerView.setAdapter(adapters);

        Bundle bundle = getIntent().getExtras();
        vn = bundle.getString("vn");

        fetchLikeUsers = new FetchLikeUsers(vn,adapters,tv,progressBar);
        fetchLikeUsers.updateVoiceNoteNumberOfLikes();
    }



}
