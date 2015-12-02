package net.beepinc.vip.Information;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tayo on 10/27/2015.
 */
public class comment_information implements Parcelable{

    public String post, image,username, comment, time,mobile;

    public comment_information(String vn,String img,String mob, String username, String comment, String tym){
        this.post = vn;
        this.image = img;
        this.username = username;
        this.comment = comment;
        this.time = tym;
        this.mobile = mob;
    }

    public comment_information(){

    }

    public comment_information(Parcel parcel){
        post = parcel.readString();
        image = parcel.readString();
        mobile = parcel.readString();
        username = parcel.readString();
        comment = parcel.readString();
        time = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(post);
        dest.writeString(image);
        dest.writeString(mobile);
        dest.writeString(username);
        dest.writeString(comment);
        dest.writeString(time);
    }

    public static final Parcelable.Creator<comment_information> CREATOR = new Parcelable.Creator<comment_information>(){
        public comment_information createFromParcel(Parcel in){
            return new comment_information(in);
        }

        public comment_information[] newArray(int size){
            return new comment_information[size];
        }

    };
}
