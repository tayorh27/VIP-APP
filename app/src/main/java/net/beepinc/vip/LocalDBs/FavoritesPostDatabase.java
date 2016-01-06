package net.beepinc.vip.LocalDBs;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import net.beepinc.vip.Information.mypost_information;

import java.util.ArrayList;

/**
 * Created by tayo on 1/5/2016.
 */
public class FavoritesPostDatabase {

    private FavoriteHelper favHelper;
    private SQLiteDatabase mDabtabase;

    public FavoritesPostDatabase(Context context){
        favHelper = new FavoriteHelper(context);
        mDabtabase = favHelper.getWritableDatabase();
    }

    public void insertMyFavorites(ArrayList<mypost_information> lists, boolean clearPrevious) {
        if (clearPrevious) {
            deleteAll();
        }
        String sql = "INSERT INTO " + FavoriteHelper.TABLE_NAME_MYFAV + " VALUES(?,?,?,?,?,?,?,?,?);";
        //compile statement and start a transaction
        SQLiteStatement statement = mDabtabase.compileStatement(sql);
        mDabtabase.beginTransaction();

        for (int i = 0; i < lists.size(); i++) {
            mypost_information current = lists.get(i);
            statement.clearBindings();

            statement.bindString(2, current.caption);
            statement.bindString(3, current.voicenote);
            statement.bindString(4, current.image);
            statement.bindString(5, current.mobile);
            statement.bindString(6, current.username);
            statement.bindString(7, current.Response_icon);
            statement.bindString(8, current.time);
            statement.bindString(9, current.display);
            statement.execute();
        }
        mDabtabase.setTransactionSuccessful();
        mDabtabase.endTransaction();
    }

    public ArrayList<mypost_information> getAllMyFavorites(String mobile) {
        ArrayList<mypost_information> currentData = new ArrayList<>();

        String[] columns = {
                FavoriteHelper.COLUMN_UID,
                FavoriteHelper.COLUMN_CAPTION,
                FavoriteHelper.COLUMN_VOICE,
                FavoriteHelper.COLUMN_IMAGE,
                FavoriteHelper.COLUMN_MOBILE,
                FavoriteHelper.COLUMN_USERNAME,
                FavoriteHelper.COLUMN_STATUS,
                FavoriteHelper.COLUMN_TIME,
                FavoriteHelper.COLUMN_DISPLAY
        };
        Cursor cursor = mDabtabase.query(FavoriteHelper.TABLE_NAME_MYFAV, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                mypost_information current = new mypost_information();
                if(cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_MOBILE)).contentEquals(mobile)) {
                    current.get_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_UID)));
                    current.caption = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_CAPTION));
                    current.voicenote = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_VOICE));
                    current.image = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_IMAGE));
                    current.mobile = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_MOBILE));
                    current.username = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_USERNAME));
                    current.Response_icon = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_STATUS));
                    current.time = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_TIME));
                    current.display = cursor.getString(cursor.getColumnIndex(FavoriteHelper.COLUMN_DISPLAY));
                    currentData.add(current);
                }
            }
            cursor.close();
        }

        return currentData;
    }

    public int getLastId(){
        int id = 1;
        String[] columns = {
                FavoriteHelper.COLUMN_UID,
                FavoriteHelper.COLUMN_CAPTION,
                FavoriteHelper.COLUMN_VOICE,
                FavoriteHelper.COLUMN_IMAGE,
                FavoriteHelper.COLUMN_MOBILE,
                FavoriteHelper.COLUMN_USERNAME,
                FavoriteHelper.COLUMN_STATUS,
                FavoriteHelper.COLUMN_TIME,
                FavoriteHelper.COLUMN_DISPLAY
        };
        Cursor cursor = mDabtabase.query(FavoriteHelper.TABLE_NAME_MYFAV, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToLast();
            id = cursor.getInt(0);
            //cursor.close();
        }
        Log.e("gotID", "my id is " + id);
        return id;
    }

    public void deleteAll() {
        mDabtabase.delete(FavoriteHelper.TABLE_NAME_MYFAV, null, null);
    }

    public void deleteDatabase(int id) {
        mDabtabase.delete(FavoriteHelper.TABLE_NAME_MYFAV,FavoriteHelper.COLUMN_UID + "="+ id,null);
    }

    public class FavoriteHelper extends SQLiteOpenHelper {

        private Context m_context;
        private static final String DB_NAME = "favoritesList_db";
        private static final int DB_VERSION = 1;

        public static final String TABLE_NAME_MYFAV = "favorites_list";
        public static final String COLUMN_UID = "_id";

        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_VOICE = "voicenote";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TIME = "created_time";
        public static final String COLUMN_DISPLAY = "display";

        private static final String CREATE_TABLE_MYFAV = "CREATE TABLE " + TABLE_NAME_MYFAV + "(" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CAPTION + " TEXT," +
                COLUMN_VOICE + " TEXT," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_MOBILE + " TEXT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_TIME + " TEXT," +
                COLUMN_DISPLAY + " TEXT" +
                ");";


        public FavoriteHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            m_context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_MYFAV);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(" DROP TABLE " + TABLE_NAME_MYFAV + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
