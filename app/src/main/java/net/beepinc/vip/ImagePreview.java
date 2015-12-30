package net.beepinc.vip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import net.beepinc.vip.json.Utils;
import net.beepinc.vip.network.VolleySingleton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;


public class ImagePreview extends ActionBarActivity {

    Toolbar toolbar;
    ImageView imageView;

    VolleySingleton volleySingleton;
    ImageLoader imageLoader;

    boolean isAvail=false, isWrite=true;
    UserLocalStore userLocalStore;
    private String username;
    private Utils utils;
    private Menu optMenu;
    private boolean sMenu = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        imageView = (ImageView)findViewById(R.id.preview);

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        userLocalStore = new UserLocalStore(this);
        utils = new Utils();

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkSDCARD();

        Bundle bundle = getIntent().getExtras();
        String imageurl = bundle.getString("image");
        username = bundle.getString("username");

        getSupportActionBar().setTitle(username);

        imageLoader.get(imageurl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                imageView.setImageBitmap(response.getBitmap());
                if(onCreateOptionsMenu(optMenu)){
                    //showMenu();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.avatar_default);
                //sMenu = false;
            }
        });

        Thread thread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if(sMenu){
                       // showMenu();
                    }
                }
            }
        };
        thread.start();

    }

    private void showMenu(){
        try {
            MenuItem mi_download = optMenu.findItem(R.id.action_download);
            MenuItem mi_share = optMenu.findItem(R.id.action_share);
            mi_download.setVisible(true);
            mi_share.setVisible(true);
        }catch (Exception e){
            Toast.makeText(ImagePreview.this,e.toString(),Toast.LENGTH_LONG).show();
        }
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

    public void saveImage(String image_name, Bitmap bitmap) {
        if (isWrite && isAvail && utils.checkSDCARDSIZEFORIMAGE() == "memory_available") {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-profile-pictures/");
            File file = new File(path, image_name);
            path.mkdirs();

            OutputStream os;

            try {
                os = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

                os.close();
                Toast.makeText(ImagePreview.this, "Image Saved to Gallery!", Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(isAvail && !isWrite){
            Toast.makeText(ImagePreview.this, "NO SPACE AVAILABLE ON SDCARD", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(ImagePreview.this, "NO SDCARD AVAILABLE", Toast.LENGTH_LONG).show();
        }
    }

    private void getAndSaveImage(){
        BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bmd.getBitmap();

        Calendar c = Calendar.getInstance();
        String tt = c.get(Calendar.HOUR) + "" + c.get(Calendar.MINUTE) + ""
                + c.get(Calendar.SECOND);
        String dt = c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + ""
                + c.get(Calendar.DAY_OF_MONTH) + "_";

        String image_name = username + "_" + dt + tt + ".jpg";

        saveImage(image_name, bitmap);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_preview, menu);
        optMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            getAndSaveImage();
        }
        if (id == R.id.action_share) {
            shareImage();
        }
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        BitmapDrawable bmd = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bmd.getBitmap();

        String path = MediaStore.Images.Media.insertImage(getContentResolver(),bitmap,"Share","Image");
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "VIP"));
        }


    }
}
