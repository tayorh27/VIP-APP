package net.beepinc.vip.Information;

import android.graphics.Bitmap;

/**
 * Created by tayo on 10/18/2015.
 */
public class mypost_information {

    public String caption, voicenote, image, mobile, username;
    public String Response_icon, time, uploaded;
    public int get_id;

    public mypost_information(int id,String cap, String v, String img, String mob, String u, String ri, String created_time, String uploaded){
        this.get_id = id;
        this.caption = cap;
        this.voicenote = v;
        this.image = img;
        this.mobile = mob;
        this.username = u;
        this.Response_icon = ri;
        this.time = created_time;
        this.uploaded = uploaded;
    }

    public mypost_information(){

    }
}
