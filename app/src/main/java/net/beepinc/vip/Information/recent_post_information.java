package net.beepinc.vip.Information;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tayo on 10/25/2015.
 */
public class recent_post_information implements Parcelable{

    public String caption, voicenote, image, mobile, username, time, id, uploaded;

    public String nComments,nLikes, nLikeUser;

    public recent_post_information(String cap, String v, String img, String mob, String u, String created_time, String Id, String uploaded, String nComments,String nLikes, String nLikeUser){
        this.caption = cap;
        this.voicenote = v;
        this.image = img;
        this.mobile = mob;
        this.username = u;
        this.time = created_time;
        this.id = Id;
        this.uploaded = uploaded;
        this.nComments =nComments;
        this.nLikes=nLikes;
        this.nLikeUser = nLikeUser;
    }

    public recent_post_information(){

    }

    public recent_post_information(Parcel parcel){
        caption = parcel.readString();
        voicenote = parcel.readString();
        image = parcel.readString();
        mobile = parcel.readString();
        username = parcel.readString();
        time = parcel.readString();
        id = parcel.readString();
        uploaded = parcel.readString();
        nComments = parcel.readString();
        nLikes =parcel.readString();
        nLikeUser = parcel.readString();
    }



    public static final Parcelable.Creator<recent_post_information> CREATOR = new Parcelable.Creator<recent_post_information>(){
        public recent_post_information createFromParcel(Parcel in){
            return new recent_post_information(in);
        }

        public recent_post_information[] newArray(int size){
            return new recent_post_information[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(voicenote);
        dest.writeString(image);
        dest.writeString(mobile);
        dest.writeString(username);
        dest.writeString(time);
        dest.writeString(id);
        dest.writeString(uploaded);
        dest.writeString(nComments);
        dest.writeString(nLikes);
        dest.writeString(nLikeUser);
    }
}
