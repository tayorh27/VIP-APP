package net.beepinc.vip;

import net.beepinc.vip.Information.favorites_information;

import java.util.List;

/**
 * Created by tayo on 8/15/2015.
 */
public class User {

    public String uname,mob,image,cate,pass,savedImg;
    public int id;
    public String sq, sa;

    public User(int id,String username, String mobile, String Image, String category, String Password){
        this.uname=username;
        this.mob=mobile;
        this.image = Image;
        this.cate = category;
        this.pass = Password;
        this.id = id;
    }

    public User(int id,String username, String mobile, String Image, String category, String Password, String security_q, String security_a){
        this.uname=username;
        this.mob=mobile;
        this.image = Image;
        this.cate = category;
        this.pass = Password;
        this.id = id;
        this.sq = security_q;
        this.sa = security_a;
    }

//    public User(String username, String mobile, String Image, String category, String Password, String savedImage){
//        this.uname=username;
//        this.mob=mobile;
//        this.image = Image;
//        this.cate = category;
//        this.pass = Password;
//        this.savedImg = savedImage;
//    }

    public User(String username, String Password){
        this.uname=username;
        this.pass = Password;
    }


}
