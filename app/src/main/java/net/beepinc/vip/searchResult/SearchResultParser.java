package net.beepinc.vip.searchResult;

import android.telephony.PhoneNumberUtils;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.recent_post_information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 1/18/2016.
 */
public class SearchResultParser {

    public static ArrayList<recent_post_information> parseJSONResponse(JSONArray jsonArray) {

        ArrayList<recent_post_information> customData = new ArrayList<>();

        String serverUrl = AppConfig.web_url + "images/";
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
        List<String> duration = new ArrayList<>();

        String[] caps, vn, imgs, mobs, uses, time, ids, ups, coms, liks, lUsers, dur;

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
                    duration.add(json.getString("duration"));
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
            dur = new String[jsonArray.length()];

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
                lUsers[j] = customLikeUsers.get(j);
                dur[j] = duration.get(j);
            }

            for (int i = 0; i < mobs.length; i++) {

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
                String get_duration = dur[i];

                recent_post_information current = new recent_post_information(get_caption, get_vn, get_image, get_mobile, get_user, get_time, get_id, get_upload, get_comment, get_likes, get_like_users, get_duration);

                customData.add(current);


            }

        } else {
        }

        return customData;
    }
}
