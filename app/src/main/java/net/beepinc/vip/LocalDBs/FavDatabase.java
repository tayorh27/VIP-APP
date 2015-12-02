package net.beepinc.vip.LocalDBs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import net.beepinc.vip.Information.favorites_information;

import java.util.ArrayList;

/**
 * Created by tayo on 10/3/2015.
 */
public class FavDatabase {
    private FavHelper favHelper;
    private SQLiteDatabase mDabtabase;

    public FavDatabase(Context context) {
        favHelper = new FavHelper(context);
        mDabtabase = favHelper.getWritableDatabase();
    }

    public void insertFavorites(ArrayList<favorites_information> lists, boolean clearPrevious) {
        if (clearPrevious) {
            deleteAll();
        }
        String sql = "INSERT INTO " + FavHelper.TABLE_NAME_FAV + " VALUES(?,?,?,?,?,?);";
        //compile statement and start a transaction
        SQLiteStatement statement = mDabtabase.compileStatement(sql);
        mDabtabase.beginTransaction();

        for (int i = 0; i < lists.size(); i++) {
            favorites_information current = lists.get(i);
            statement.clearBindings();

            statement.bindString(2, current.id);
            statement.bindString(3, current.title);
            statement.bindString(4, current.subtitle);
            statement.bindString(5, current.category);
            statement.bindString(6, current.image_name);
            statement.execute();
        }
        mDabtabase.setTransactionSuccessful();
        mDabtabase.endTransaction();
    }

    public ArrayList<favorites_information> getAllFavorites() {
        ArrayList<favorites_information> currentData = new ArrayList<>();

        String[] columns = {
                FavHelper.COLUMN_UID,
                FavHelper.COLUMN_GID,
                FavHelper.COLUMN_USERNAME,
                FavHelper.COLUMN_MOBILE,
                FavHelper.COLUMN_CATEGORY,
                FavHelper.COLUMN_IMAGE_NAME
        };
        Cursor cursor = mDabtabase.query(FavHelper.TABLE_NAME_FAV, columns, null, null, null,null, null);
        if(cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                favorites_information current = new favorites_information();
                current.id = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_GID));
                current.title = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_USERNAME));
                current.subtitle = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_MOBILE));
                current.category = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_CATEGORY));
                current.image_name = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_IMAGE_NAME));
                currentData.add(current);
            }
            cursor.close();
        }

        return currentData;
    }

    public void deleteAll() {
        mDabtabase.delete(FavHelper.TABLE_NAME_FAV, null, null);
    }


    public class FavHelper extends SQLiteOpenHelper {

        private Context mcontext;
        private static final String DB_NAME = "favorites_db";
        private static final int DB_VERSION = 1;

        public static final String TABLE_NAME_FAV = "favorites";
        public static final String COLUMN_UID = "_id";

        public static final String COLUMN_GID = "gid";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IMAGE_NAME = "image";

        private static final String CREATE_TABLE_FAV = "CREATE TABLE " + TABLE_NAME_FAV + "(" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_GID + " TEXT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_MOBILE + " TEXT," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_IMAGE_NAME + " TEXT" +
                ");";


        public FavHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mcontext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_FAV);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(" DROP TABLE " + TABLE_NAME_FAV + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
