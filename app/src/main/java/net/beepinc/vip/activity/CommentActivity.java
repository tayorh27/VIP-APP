package net.beepinc.vip.activity;

import android.os.PersistableBundle;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.Information.comment_information;
import net.beepinc.vip.InternetChecking;
import net.beepinc.vip.MyAdapters.comment_adapters;
import net.beepinc.vip.R;
import net.beepinc.vip.Social.Comments;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.callback.CommentsLoadedLisener;
import net.beepinc.vip.taskComment.TaskLoadComments;

import java.util.ArrayList;

public class CommentActivity extends ActionBarActivity implements View.OnClickListener, CommentsLoadedLisener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar, progressBar1;
    private TextView textView;
    private EditText editText;
    private Button button;

    private Comments comments;
    private UserLocalStore userLocalStore;
    private String post;

    ArrayList<comment_information> customList = new ArrayList<>();

    private comment_adapters adapters;

    InternetChecking isConn;
    boolean icheck = false;
    ActionBar actionBar;
    private String STATE_KEY = "comments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        adapters.setList(customList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
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
        }
    }

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
