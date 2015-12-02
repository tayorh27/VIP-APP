package net.beepinc.vip.recent_json;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.Information.recent_post_information;
import net.beepinc.vip.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 10/25/2015.
 */
public class Parser {

    private static Cursor cursor;

    private static List<String> cus_names = new ArrayList<>();
    private static List<String> cus_phones = new ArrayList<>();

    private static List<String> c_phones = new ArrayList<>();
    private static Context context = MyApplication.getAppContext();

    public static void performContactTask() {

        cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contact_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contact_phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            cus_names.add(contact_name);
            cus_phones.add(contact_phone);
        }

        cursor.close();
    }

    public static void sortAll(){

        performContactTask();

        for (int i = 0; i < cus_phones.size(); i++){
            for (int j = 1; j < cus_phones.size(); j++){

                if(cus_phones.get(i).contentEquals(cus_phones.get(j))){
                    cus_phones.remove(j);
                }

            }
        }
        for (int k = 0; k < cus_phones.size(); k++){
            c_phones.add(cus_phones.get(k));
        }
    }


    public static ArrayList<recent_post_information> parseJSONResponse(JSONArray jsonArray, String get_mob) {

        c_phones.clear();
        cus_names.clear();
        c_phones.clear();

        sortAll();
        //caption, voicenote, image, mobile, username, time,id;
        ArrayList<recent_post_information> customData = new ArrayList<>();

        String serverUrl = "http://gisanrinadetayo.comuf.com/images/";
        List<String> customCaption = new ArrayList<>();
        List<String> customVoicenote = new ArrayList<>();
        List<String> customImage = new ArrayList<>();
        List<String> customMobile = new ArrayList<>();
        List<String> customUsername = new ArrayList<>();
        List<String> customTime = new ArrayList<>();
        List<String> customId = new ArrayList<>();
        List<String> customUpload = new ArrayList<>();
        List<String> customComment = new ArrayList<>();
        List<String> customLikes = new ArrayList<>();
        List<String> customLikeUsers = new ArrayList<>();

        String[] caps, vn, imgs, mobs, uses, time, ids,ups,coms,liks,lUsers;

        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject json = jsonArray.getJSONObject(i);
                    customCaption.add(json.getString("caption"));
                    customVoicenote.add(json.getString("notes"));
                    customImage.add(json.getString("image"));
                    customMobile.add(json.getString("mobile"));
                    customUsername.add(json.getString("username"));
                    customTime.add(json.getString("time"));
                    customId.add(json.getString("id"));
                    customUpload.add(json.getString("uploaded"));
                    customComment.add(json.getString("number_of_comments"));
                    customLikes.add(json.getString("number_of_likes"));
                    customLikeUsers.add(json.getString("likeUsers"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            caps = new String[jsonArray.length()];
            vn = new String[jsonArray.length()];
            imgs = new String[jsonArray.length()];
            mobs = new String[jsonArray.length()];
            uses = new String[jsonArray.length()];
            time = new String[jsonArray.length()];
            ids = new String[jsonArray.length()];
            ups = new String[jsonArray.length()];
            coms = new String[jsonArray.length()];
            liks = new String[jsonArray.length()];
            lUsers = new String[jsonArray.length()];

            for (int j = 0; j < customUsername.size(); j++) {

                caps[j] = customCaption.get(j);
                vn[j] = customVoicenote.get(j);
                imgs[j] = customImage.get(j);
                mobs[j] = customMobile.get(j);
                uses[j] = customUsername.get(j);
                time[j] = customTime.get(j);
                ids[j] = customId.get(j);
                ups[j] = customUpload.get(j);
                coms[j] = customComment.get(j);
                liks[j] = customLikes.get(j);
                lUsers[j]  = customLikeUsers.get(j);
            }

            for (int i = 0; i < mobs.length; i++) {
                for (int j = 0; j < c_phones.size(); j++) {
                    if (PhoneNumberUtils.compare(context,mobs[i],c_phones.get(j))){// mobs[i].contains(c_phones.get(j))) {

                        String get_caption = caps[i];
                        String get_vn = vn[i];
                        String get_image = imgs[i];
                        String get_mobile = mobs[i];
                        String get_user = uses[i];
                        String get_time = time[i];
                        String get_id = ids[i];
                        String get_upload = ups[i];
                        String get_comment = coms[i];
                        String get_likes = liks[i];
                        String get_like_users = lUsers[i];

                        recent_post_information current = new recent_post_information(get_caption, get_vn, get_image, get_mobile, get_user, get_time, get_id, get_upload,get_comment,get_likes,get_like_users);

                        customData.add(current);


                    }
                }

            }
//            for(int k = 0; k < mobs.length; k++){
//                if(mobs[k].contains(get_mob)){
//                    String get_caption = caps[k];
//                    String get_vn = vn[k];
//                    String get_image = imgs[k];
//                    String get_mobile = mobs[k];
//                    String get_user = uses[k];
//                    String get_time = time[k];
//                    String get_id = ids[k];
//
//                    recent_post_information current = new recent_post_information(get_caption, get_vn, get_image, get_mobile, get_user, get_time, get_id);
//
//                    customData.add(current);
//                }
//            }

        } else {
        }

        return customData;
    }
}
