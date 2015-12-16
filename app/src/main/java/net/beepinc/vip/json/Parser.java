package net.beepinc.vip.json;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.MyApplication;
import net.beepinc.vip.User;
import net.beepinc.vip.UserLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tayo on 10/4/2015.
 */
public class Parser {

    private static Cursor cursor;

    private static List<String> cus_names = new ArrayList<>();
    private static List<String> cus_phones = new ArrayList<>();

    private static List<String> c_phones = new ArrayList<>();
    private static Context context = MyApplication.getAppContext();
    private static UserLocalStore userLocalStore = new UserLocalStore(context);

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

    public static void sortAll() {

        performContactTask();

        for (int i = 0; i < cus_phones.size(); i++) {
            for (int j = 1; j < cus_phones.size(); j++) {

                if (cus_phones.get(i).contentEquals(cus_phones.get(j))) {
                    cus_phones.remove(j);
                }

            }
        }
        for (int k = 0; k < cus_phones.size(); k++) {
            c_phones.add(cus_phones.get(k));
        }
    }

    public static ArrayList<favorites_information> parseJSONResponse(JSONArray jsonArray) {

        c_phones.clear();
        cus_phones.clear();

        sortAll();

        ArrayList<favorites_information> customData = new ArrayList<>();

        String serverUrl = AppConfig.web_url+"images/";
        List<String> customUsername = new ArrayList<>();
        List<String> customMobile = new ArrayList<>();
        List<String> customImage = new ArrayList<>();
        List<String> customCategory = new ArrayList<>();
        List<String> customId = new ArrayList<>();

        String[] uses, mobs, imgs, cats, ids;

        if (jsonArray != null && jsonArray.length() > 0) {

            for (int i = 0; i < jsonArray.length(); i++) {

                try {
                    JSONObject json = jsonArray.getJSONObject(i);

                    customId.add(json.getString("id"));
                    customUsername.add(json.getString("username"));
                    customMobile.add(json.getString("mobile"));
                    customImage.add(json.getString("image"));
                    customCategory.add(json.getString("category"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            uses = new String[jsonArray.length()];
            mobs = new String[jsonArray.length()];
            imgs = new String[jsonArray.length()];
            cats = new String[jsonArray.length()];
            ids = new String[jsonArray.length()];

            for (int j = 0; j < customUsername.size(); j++) {

                uses[j] = customUsername.get(j);
                mobs[j] = customMobile.get(j);
                imgs[j] = customImage.get(j);
                cats[j] = customCategory.get(j);
                ids[j] = customId.get(j);
            }
            User user = userLocalStore.getLoggedUser();
            String userMobile = user.mob;
            for (int i = 0; i < mobs.length; i++) {
                for (int j = 0; j < c_phones.size(); j++) {
                    if (PhoneNumberUtils.compare(context,mobs[i],c_phones.get(j))) {

                        String get_id = ids[i];
                        String get_username = uses[i];
                        String get_mobile = mobs[i];
                        String get_image = serverUrl + Uri.encode(imgs[i]);
                        String get_category = cats[i];

                        favorites_information current = new favorites_information(get_id, get_username, get_mobile, get_category, get_image);
                        customData.add(current);

                    }
                }

            }

        } else {
            customData = MyApplication.getWriteableDatabase().getAllFavorites();
        }

        return customData;
    }
}
