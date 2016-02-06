package net.beepinc.vip.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.MyAdapters.favorites_adapters;
import net.beepinc.vip.R;
import net.beepinc.vip.callback.SearchLoadedListener;
import net.beepinc.vip.task.TaskLoadSearch;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends ActionBarActivity implements favorites_adapters.Clicklistener, SearchLoadedListener {

    RecyclerView recyclerView;
    EditText editText;
    TextView tv;
    ProgressBar progressBar;
    favorites_adapters adapters;
    Toolbar toolbar;
    private ArrayList<favorites_information> customList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.searchContact);
        tv = (TextView)findViewById(R.id.textView3);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        adapters = new favorites_adapters(SearchActivity.this,"hide");
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        recyclerView.setAdapter(adapters);

        adapters.setClicklistener(this);

        tv.setText("Search user");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = String.valueOf(s);
                execute(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void execute(String text){
        progressBar.setVisibility(View.VISIBLE);
        new TaskLoadSearch(this,text).execute();
    }


    @Override
    public void ItemClick(View view, int Position) {
        //open new activity
        String name = customList.get(Position).title;
        String category = customList.get(Position).category;
        String image = customList.get(Position).image_name;

        Intent intent = new Intent(SearchActivity.this,UserActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uname",name);
        bundle.putString("ucategory",category);
        bundle.putString("uimage",image);

        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onSearchLoadedListener(ArrayList<favorites_information> list) {
        if(list.isEmpty()){
            tv.setVisibility(View.VISIBLE);
            tv.setText("User not found");
            progressBar.setVisibility(View.GONE);
        }
        adapters.setList(list);
        customList = list;
        progressBar.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
    }
}
