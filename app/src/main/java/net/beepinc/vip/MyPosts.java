package net.beepinc.vip;


import android.os.Bundle;
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

import java.util.ArrayList;

/**
 * Created by tayo on 9/15/2015.
 */
public class MyPosts extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyPostsLoadedListener {

    RecyclerView recyclerView;
    mypost_adapters adapters;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tv;
    ArrayList<mypost_information> customList = new ArrayList<>();
    private UserLocalStore userLocalStore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View row = inflater.inflate(R.layout.myposts, container, false);

        recyclerView = (RecyclerView)row.findViewById(R.id.mypostView);
        tv = (TextView)row.findViewById(R.id.my_post_tv);
        swipeRefreshLayout = (SwipeRefreshLayout)row.findViewById(R.id.swipe);
        adapters = new mypost_adapters(getActivity());
        userLocalStore = new UserLocalStore(getActivity());
        User user = userLocalStore.getLoggedUser();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapters);

        customList = MyApplication.getWriteableDatabaseForMyPosts().getAllMyPosts(user.mob);
        if(!customList.isEmpty()){
            tv.setVisibility(View.GONE);
        }
        adapters.setList(customList);

        try {
            //new TaskLoadMyPosts(this).execute();

        }catch (Exception e){

        }


        swipeRefreshLayout.setOnRefreshListener(this);
        return row;
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
}
