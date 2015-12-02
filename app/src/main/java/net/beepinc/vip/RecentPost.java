package net.beepinc.vip;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.MyAdapters.recentposts_adapters;
import net.beepinc.vip.callback.RecentPostsLoadedListener;
import net.beepinc.vip.task.TaskLoadFavorites;
import net.beepinc.vip.task.TaskLoadRecentPosts;

import java.util.ArrayList;

/**
 * Created by tayo on 9/15/2015.
 */
public class RecentPost extends Fragment implements RecentPostsLoadedListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textView;
    ArrayList<recent_post_information> customList = new ArrayList<>();
    private UserLocalStore userLocalStore;
    private recentposts_adapters adapters;

    InternetChecking isConn;
    boolean icheck = false;
    private String STATE_KEY = "recent_posts";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .permitAll().build();
//        StrictMode.setThreadPolicy(policy);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.recent_post, container, false);

        recyclerView = (RecyclerView)row.findViewById(R.id.recent_post_View);
        progressBar = (ProgressBar)row.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout)row.findViewById(R.id.swipe);
        textView = (TextView)row.findViewById(R.id.textView);

        isConn = new InternetChecking(getActivity());
        icheck = isConn.isConnectedToInternet();

        userLocalStore = new UserLocalStore(getActivity());
        User user = userLocalStore.getLoggedUser();
        if (!icheck) {
            //Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            textView.setText("Oooop no internet");
        }

        adapters = new recentposts_adapters(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapters);

        try {
            if(savedInstanceState != null){
                progressBar.setVisibility(View.GONE);
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            }else {
                if(customList.isEmpty()) {
                    new TaskLoadRecentPosts(this, user.mob).execute();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        adapters.setLists(customList);
        swipeRefreshLayout.setOnRefreshListener(this);

        return row;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, customList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        try {
            if(savedInstanceState != null){
                progressBar.setVisibility(View.GONE);
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
            //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRecentPostsLoadedListener(ArrayList<recent_post_information> lists) {

        adapters.setLists(lists);
        progressBar.setVisibility(View.GONE);
        if (lists.isEmpty()){
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
        }
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        customList.clear();
        customList = lists;
    }

    @Override
    public void onRefresh() {
        icheck = isConn.isConnectedToInternet();
        User user = userLocalStore.getLoggedUser();
        if (icheck) {
            new TaskLoadRecentPosts(this,user.mob).execute();
        } else {
            //Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            textView.setVisibility(View.VISIBLE);
            textView.setText("Oooop no internet");
            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }
}
