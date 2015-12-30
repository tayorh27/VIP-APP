package net.beepinc.vip;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by tayo on 12/14/2015.
 */
public class AppConfig {

    public static String web_url = "http://www.bistelint.com.ng/vip/";

    public static void ReplaceDefaultFont(Context context, String nameOfFont, String nameOfFontInAsset){
        Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(),nameOfFontInAsset);
        replaceFont(nameOfFont,customFontTypeface);
    }

    private static void replaceFont(String nameOfFont, Typeface customFontTypeface) {
        try {
            Field myField = Typeface.class.getDeclaredField(nameOfFont);
            myField.setAccessible(true);
            myField.set(null,customFontTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
