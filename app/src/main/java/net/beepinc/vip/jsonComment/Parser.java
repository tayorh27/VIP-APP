package net.beepinc.vip.jsonComment;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.comment_information;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 10/27/2015.
 */
public class Parser {

    public static ArrayList<comment_information> parseJSONResponse(JSONArray jsonArray, String current_post) {


        ArrayList<comment_information> customData = new ArrayList<>();

        String serverUrl = AppConfig.web_url+"images/";
        List<String> customPost = new ArrayList<>();
        List<String> customImage = new ArrayList<>();
        List<String> customMobile = new ArrayList<>();
        List<String> customUsername = new ArrayList<>();
        List<String> customComment = new ArrayList<>();
        List<String> customTime = new ArrayList<>();


        String[] posts, imgs, mobs, uses, coms, times;

        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject json = jsonArray.getJSONObject(i);

                    customPost.add(json.getString("post"));
                    customImage.add(json.getString("image"));
                    customMobile.add(json.getString("mobile"));
                    customUsername.add(json.getString("username"));
                    customComment.add(json.getString("comment"));
                    customTime.add(json.getString("time"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            posts = new String[jsonArray.length()];
            imgs = new String[jsonArray.length()];
            mobs = new String[jsonArray.length()];
            uses = new String[jsonArray.length()];
            coms = new String[jsonArray.length()];
            times = new String[jsonArray.length()];

            for (int j = 0; j < customPost.size(); j++) {

                posts[j] = customPost.get(j);
                imgs[j] = customImage.get(j);
                mobs[j] = customMobile.get(j);
                uses[j] = customUsername.get(j);
                coms[j] = customComment.get(j);
                times[j] = customTime.get(j);
            }

            for (int i = 0; i < posts.length; i++) {
                if (posts[i].contentEquals(current_post)) {

                    String get_post = posts[i];
                    String get_image = imgs[i];
                    String get_mobile = mobs[i];
                    String get_username = uses[i];
                    String get_comment = coms[i];
                    String get_time = times[i];

                    comment_information current = new comment_information(get_post, get_image, get_mobile, get_username, get_comment, get_time);
                    customData.add(current);
                }
            }

        } else {
        }

        return customData;
    }
}
