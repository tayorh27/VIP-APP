package net.beepinc.vip.LocalDBs;

/**
 * Created by tayo on 9/27/2015.
 */
public class StoreFavorites {

    public String usernames,mobiles,categories,images,ids;
    String mee;
    public StoreFavorites(String s_usernames, String s_mobiles, String s_categories, String s_images, String s_ids){
        this.usernames = s_usernames;
        this.mobiles = s_mobiles;
        this.categories = s_categories;
        this.images = s_images;
        this.ids = s_ids;
    }
}
