package net.beepinc.vip;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Homeview extends ActionBarActivity {

    ImageView next;
    Button signup, login;
    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeview);

        next = (ImageView)findViewById(R.id.logo);
        signup = (Button)findViewById(R.id.btn_signup);
        login = (Button)findViewById(R.id.btn_login);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.marvelironman);
        next.setImageBitmap(icon);
        //test.setImageBitmap(icon);

        userLocalStore = new UserLocalStore(this);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homeview.this, Signup.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Homeview.this, Loginview.class);
                startActivity(i);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                View preview;
//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                preview = inflater.inflate(R.layout.image_preview, (android.view.ViewGroup) findViewById(R.id.root));
//                ImageView ivv = (ImageView)preview.findViewById(R.id.imageView1);

                //BitmapDrawable bmd = (BitmapDrawable) test.getDrawable();
                //Bitmap bitmap = bmd.getBitmap();

                //ivv.setImageBitmap(bitmap);
//                AlertDialog ad = new AlertDialog.Builder(Homeview.this).create();
//                ad.setContentView(R.layout.image_preview);
//                ad.setTitle("tayo");
//                ad.show();
                Intent intent = new Intent(Homeview.this, MainActivity.class);
               startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent;
        if (authenticate() == true){
            intent = new Intent(Homeview.this, MainActivity.class);
            startActivity(intent);
        }else {
//            intent = new Intent(Homeview.this, Signup.class);
//            startActivity(intent);
            return;
        }
    }

    public boolean authenticate(){
        return userLocalStore.getUserLoggedIn();
    }
}
