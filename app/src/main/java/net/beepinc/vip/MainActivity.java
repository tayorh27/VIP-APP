package net.beepinc.vip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import net.beepinc.vip.activity.FavoriteContacts;
import net.beepinc.vip.activity.RecordingActivity;
import net.beepinc.vip.services.MyService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends ActionBarActivity {

    private static final int JOB_ID = 100;
    private static final long POLL_FREQUENCY = 1000;
    //TextView tv;
    UserLocalStore userLocalStore;
    Toolbar toolbar;
    //ImageView iv;
    //FloatingActionButton fab;
    boolean isConn = false;
    InternetChecking internetChecking;
    ViewPager pager;
    SlidingTabLayout tab;
    private JobScheduler jobScheduler;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internetChecking = new InternetChecking(MainActivity.this);
        isConn = internetChecking.isConnectedToInternet();
        //tv = (TextView) findViewById(R.id.displayme);
        userLocalStore = new UserLocalStore(this);
        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        //iv = (ImageView) findViewById(R.id.dp);
        //fab = (FloatingActionButton) findViewById(R.id.fab);

        pager = (ViewPager)findViewById(R.id.pager);
        tab = (SlidingTabLayout)findViewById(R.id.tabs);

        pager.setAdapter(new MyViewPager(getSupportFragmentManager()));
        tab.setDistributeEvenly(true);
        tab.setBackgroundColor(getResources().getColor(R.color.mainColor));
        tab.setSelectedIndicatorColors(getResources().getColor(R.color.submainColor));
        tab.setViewPager(pager);

        setSupportActionBar(toolbar);
        final User user = userLocalStore.getLoggedUser();

        jobScheduler = JobScheduler.getInstance(this);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                constructJob();
//            }
//        }, 1000);

        getSupportActionBar().setSubtitle("welcome "+user.uname);//+"-"+user.pass+"-"+user.id);

        long sdcard = Environment.getExternalStorageDirectory().getFreeSpace();
        //Toast.makeText(MainActivity.this, "Free space on sdcard = "+sdcard,Toast.LENGTH_LONG).show();


        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.mic_icon);



        com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this)
                .setContentView(imageView)
                .setBackgroundDrawable(R.drawable.btn_selector)
                .build();


        ImageView imageView1 = new ImageView(this);
        imageView1.setImageResource(R.drawable.marvelironman);

        ImageView imageView2 = new ImageView(this);
        imageView2.setImageResource(R.drawable.avatar_default);

        ImageView imageView3 = new ImageView(this);
        imageView3.setImageResource(R.drawable.ic_launcher);

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        itemBuilder.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_selector));

        SubActionButton button1 = itemBuilder.setContentView(imageView1).build();
        SubActionButton button2 = itemBuilder.setContentView(imageView2).build();
        SubActionButton button3 = itemBuilder.setContentView(imageView3).build();



        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .attachTo(actionButton)
                .build();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Record", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, RecordingActivity.class));
            }
        });

    }

    private void constructJob(){
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, MyService.class));
        builder.setPeriodic(POLL_FREQUENCY)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                .setPersisted(true);

        jobScheduler.schedule(builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.favorites){
            startActivity(new Intent(MainActivity.this, FavoriteContacts.class));
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            userLocalStore.clearUserDatabase();
            startActivity(new Intent(MainActivity.this, Homeview.class));
            finish();
        }
        if (id == R.id.edit_profile){
            startActivity(new Intent(MainActivity.this, UpdateActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DisplayDetails();
    }

    public void DisplayDetails() {
        User user = userLocalStore.getLoggedUser();

        //tv.setText(user.uname + "\n" + user.mob + "\n" + user.cate + "\n" + user.image + "\n" + user.pass);
    }


    class MyViewPager extends FragmentPagerAdapter{

        String[] mTabs;
        public MyViewPager(FragmentManager fm) {
            super(fm);
            mTabs = getResources().getStringArray(R.array.mtabs);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return  mTabs[position];
        }

        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return new RecentPost();
                case 1:
                    return new MyPosts();
            }
            return null;
        }
    }

    public static class MyFragment extends Fragment{

        public static MyFragment getInstance(int position){
            MyFragment myFragment = new MyFragment();

            Bundle args = new Bundle();
            args.putInt("position", position);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.image_preview, container, false);

            return layout;
        }
    }

    public Bitmap createCircleBitmap(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2 + 0.7f,
                bitmapimg.getHeight() / 2 + 0.7f, bitmapimg.getWidth() / 2 + 0.7f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }
}
