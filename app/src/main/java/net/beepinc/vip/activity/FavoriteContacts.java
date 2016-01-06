package net.beepinc.vip.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.InternetChecking;
import net.beepinc.vip.MyAdapters.favorites_adapters;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.R;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;
import net.beepinc.vip.callback.FavoritesLoadedListener;
import net.beepinc.vip.task.TaskLoadFavorites;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FavoriteContacts extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener, favorites_adapters.Clicklistener, FavoritesLoadedListener {

    private static final String STATE_KEY = "favorites";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    TextView textView;
    favorites_adapters adapters;
    UserLocalStore userLocalStore;
    Toolbar toolbar;

    InternetChecking isConn;
    boolean icheck = false;
    private ArrayList<favorites_information> customList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_contacts);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        replaceFont();

        recyclerView = (RecyclerView) findViewById(R.id.favoriteView);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavoriteContacts.this));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textView = (TextView)findViewById(R.id.textView2);
        userLocalStore = new UserLocalStore(FavoriteContacts.this);
        adapters = new favorites_adapters(FavoriteContacts.this,"show");
        User user = userLocalStore.getLoggedUser();
        isConn = new InternetChecking(FavoriteContacts.this);
        icheck = isConn.isConnectedToInternet();
        adapters.setClicklistener(this);
        recyclerView.setAdapter(adapters);

        swipeRefreshLayout.setOnRefreshListener(this);

        try {
            if (savedInstanceState != null) {
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
                progressBar.setVisibility(View.GONE);
            } else {
                customList = MyApplication.getWriteableDatabase().getAllFavorites();
                progressBar.setVisibility(View.GONE);
                if (customList.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    new TaskLoadFavorites(this).execute();
                }
            }
            adapters.setList(customList);
        }catch (Exception e){
            Toast.makeText(FavoriteContacts.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void replaceFont(){
        AppConfig.ReplaceDefaultFont(FavoriteContacts.this,"DEFAULT","avenir_light.ttf");
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, customList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite_contacts, menu);
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
        if(id == R.id.home){
            NavUtils.navigateUpFromSameTask(FavoriteContacts.this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        icheck = isConn.isConnectedToInternet();
        if (icheck) {
            new TaskLoadFavorites(this).execute();
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText("Oooop no internet");
            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void ItemClick(View view, int Position) {
        try {
            AlertDialog ad = new AlertDialog.Builder(FavoriteContacts.this).create();
            String name = customList.get(Position).title;
            final String phone = customList.get(Position).subtitle;
            ad.setTitle(name);
            ad.setMessage(phone);
            ad.setButton("CALL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: " + phone));
                    startActivity(intent);
                }
            });
            ad.setButton2("SMS", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms: " + phone));
                    startActivity(intent);
                }
            });
            ad.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFavoritesLoadedListener(ArrayList<favorites_information> list) {
        adapters.setList(list);
        progressBar.setVisibility(View.GONE);
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        if (list.isEmpty()){
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
        }
    }
}
