package net.beepinc.vip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.LocalDBs.StoreFavorites;
import net.beepinc.vip.MyAdapters.favorites_adapters;
import net.beepinc.vip.callback.FavoritesLoadedListener;
import net.beepinc.vip.network.VolleySingleton;
import net.beepinc.vip.services.MyService;
import net.beepinc.vip.task.TaskLoadFavorites;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tayo on 9/15/2015.
 */
public class FavoriteContact extends Fragment implements favorites_adapters.Clicklistener, FavoritesLoadedListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String STATE_KEY = "favorites";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    favorites_adapters adapters;
    Cursor cursor;
    UserLocalStore userLocalStore;

    InternetChecking isConn;
    boolean icheck = false;
    Bitmap myBitmap;

    boolean isWrite = false, isAvail = false;
    String s = "";

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private String URL_FAV = AppConfig.web_url+"FetchUserData.php";
    private JSONObject object;
    private ArrayList<favorites_information> customList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View row = inflater.inflate(R.layout.favorite, container, false);
        recyclerView = (RecyclerView) row.findViewById(R.id.favoriteView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = (SwipeRefreshLayout) row.findViewById(R.id.swipe);
        progressBar = (ProgressBar)row.findViewById(R.id.progressBar);
        userLocalStore = new UserLocalStore(getActivity());
        adapters = new favorites_adapters(getActivity());
        User user = userLocalStore.getLoggedUser();
        isConn = new InternetChecking(getActivity());
        icheck = isConn.isConnectedToInternet();
        checkSDCARD();
        adapters.setClicklistener(this);
        recyclerView.setAdapter(adapters);

        swipeRefreshLayout.setOnRefreshListener(this);

        object = null;
        try {
            if (savedInstanceState != null) {
                customList = savedInstanceState.getParcelableArrayList(STATE_KEY);
                progressBar.setVisibility(View.GONE);
            } else {
                customList = MyApplication.getWriteableDatabase().getAllFavorites();
                progressBar.setVisibility(View.GONE);
                if (customList.isEmpty()) {
                    new TaskLoadFavorites(this).execute();
                }
            }
            adapters.setList(customList);
        }catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return row;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_KEY, customList);
    }

    public void checkSDCARD() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.contentEquals(state)) {
            isAvail = true;
            isWrite = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.contentEquals(state)) {
            isAvail = true;
            isWrite = false;
        } else {
            isAvail = false;
            isWrite = false;
        }
    }

    public void saveImages(String imagename, Bitmap bitmap) {
        if (isWrite && isAvail) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-profile-pictures/");
            File file = new File(path, imagename);
            path.mkdirs();

            OutputStream os;

            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "NO SDCARD AVAILABLE", Toast.LENGTH_LONG).show();
        }
    }

    public List<favorites_information> LoadDataWithoutInternet() {
        StoreFavorites storeFavorites = userLocalStore.getFav();
        String[] usernames = splitArray(storeFavorites.usernames);
        String[] mobiles = splitArray(storeFavorites.mobiles);
        String[] category = splitArray(storeFavorites.categories);
        String[] image = splitArray(storeFavorites.images);
        String[] id = splitArray(storeFavorites.ids);

        List<favorites_information> datas = new ArrayList<>();

        for (int i = 0; i < usernames.length; i++) {

            favorites_information current = new favorites_information();

            current.title = usernames[i];
            current.subtitle = mobiles[i];
            current.category = category[i];
            current.icon = BitmapFactory.decodeFile(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-profile-pictures/" + image[i])).trim());
            datas.add(current);
        }

        return datas;

    }

    public ArrayList<favorites_information> getData() {
        User user = userLocalStore.getLoggedUser();
        List<String> names = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        ArrayList<favorites_information> data = new ArrayList<>();

        int[] images = {R.drawable.avatar_default, R.drawable.avatar_default,
                R.drawable.avatar_default, R.drawable.avatar_default, R.drawable.avatar_default};
        String[] titles = {"avatar_default0", "avatar_default1", "avatar_default2", "avatar_default3", "avatar_default4"};

        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {

            String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contact_phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            names.add(contact_name);
            phones.add(contact_phone);
        }
        cursor.close();

        for (int i = 0; i < names.size(); i++) {
            favorites_information current = new favorites_information();
            current.title = names.get(i);
            current.icon = BitmapFactory.decodeResource(getResources(), R.drawable.avatar_default);
            current.subtitle = phones.get(i);
            current.category = "Default";
            data.add(current);
        }
        //String arrays_of_titles = Arrays.toString(titles);

        //String[] my = splitArray(arrays_of_titles);

        //mtv.setText(my[4]);

        return data;
    }

    public String[] splitArray(String item) {
        String initial = item.substring(1);
        String _final = initial.substring(0, initial.lastIndexOf("]"));
        String[] arr = _final.split(",");
        return arr;
    }

    public void bitmpa_from_web(String image_name) {
        String serverUrl = AppConfig.web_url+"images/" + image_name;
        try {
            new AsyncTask<String, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    Bitmap icon = null;

                    try {
                        InputStream is = new URL(url).openStream();
                        icon = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return icon;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    //wat to do
                    myBitmap = bitmap;//Bitmap.createBitmap(bitmap);
                }

            }.execute(serverUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ItemClick(View view, int Position) {
        try {
            AlertDialog ad = new AlertDialog.Builder(getActivity()).create();
            String name = customList.get(Position).title;
            final String phone = customList.get(Position).subtitle;
            ad.setTitle(name);
            ad.setMessage(phone);
            ad.setButton("CALL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
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
        if(swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        adapters.setList(list);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        icheck = isConn.isConnectedToInternet();
        if (icheck) {
            new TaskLoadFavorites(this).execute();
        } else {
            Toast.makeText(getActivity(), "No Internet Connection!", Toast.LENGTH_LONG).show();
            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }

}
