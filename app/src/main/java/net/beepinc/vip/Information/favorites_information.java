package net.beepinc.vip.Information;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tayo on 9/19/2015.
 */
public class favorites_information implements Parcelable {

    public Bitmap icon;
    public String title;
    public String subtitle;
    public String category;
    public String id;
    public String image_name;

    public favorites_information(String id, String username, String mobile, String category, String image){
        this.id = id;
        this.title = username;
        this.subtitle = mobile;
        this.category = category;
        this.image_name = image;
    }

    public favorites_information(){

    }

    public favorites_information(Parcel input){
        id = input.readString();
        title = input.readString();
        subtitle = input.readString();
        category = input.readString();
        image_name = input.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(category);
        dest.writeString(image_name);
    }

    public static final Creator<favorites_information> CREATOR = new Creator<favorites_information>(){
        public favorites_information createFromParcel(Parcel in){
            return new favorites_information(in);
        }

        public favorites_information[] newArray(int size){
            return new favorites_information[size];
        }

    };
}
