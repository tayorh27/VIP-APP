package net.beepinc.vip.LocalDBs;

import android.content.ContentValues;
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
 * Created by tayo on 10/18/2015.
 */
public class MyPostDatabase {

    private FavHelper favHelper;
    private SQLiteDatabase mDabtabase;

    public MyPostDatabase(Context context) {
        favHelper = new FavHelper(context);
        mDabtabase = favHelper.getWritableDatabase();
    }

    public void insertMyPost(ArrayList<mypost_information> lists, boolean clearPrevious) {
        if (clearPrevious) {
            deleteAll();
        }
        String sql = "INSERT INTO " + FavHelper.TABLE_NAME_MYPOST + " VALUES(?,?,?,?,?,?,?,?);";
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
            statement.execute();
        }
        mDabtabase.setTransactionSuccessful();
        mDabtabase.endTransaction();
    }

    public ArrayList<mypost_information> getAllMyPosts(String mobile) {
        ArrayList<mypost_information> currentData = new ArrayList<>();

        String[] columns = {
                FavHelper.COLUMN_UID,
                FavHelper.COLUMN_CAPTION,
                FavHelper.COLUMN_VOICE,
                FavHelper.COLUMN_IMAGE,
                FavHelper.COLUMN_MOBILE,
                FavHelper.COLUMN_USERNAME,
                FavHelper.COLUMN_STATUS,
                FavHelper.COLUMN_TIME
        };
        Cursor cursor = mDabtabase.query(FavHelper.TABLE_NAME_MYPOST, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                mypost_information current = new mypost_information();
                if(cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_MOBILE)).contentEquals(mobile)) {
                    current.get_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_UID)));
                    current.caption = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_CAPTION));
                    current.voicenote = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_VOICE));
                    current.image = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_IMAGE));
                    current.mobile = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_MOBILE));
                    current.username = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_USERNAME));
                    current.Response_icon = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_STATUS));
                    current.time = cursor.getString(cursor.getColumnIndex(FavHelper.COLUMN_TIME));
                    currentData.add(current);
                }
            }
            cursor.close();
        }

        return currentData;
    }

    public int getLastId(){
        int id = 0;
        String[] columns = {
                FavHelper.COLUMN_UID,
                FavHelper.COLUMN_CAPTION,
                FavHelper.COLUMN_VOICE,
                FavHelper.COLUMN_IMAGE,
                FavHelper.COLUMN_MOBILE,
                FavHelper.COLUMN_USERNAME,
                FavHelper.COLUMN_STATUS,
                FavHelper.COLUMN_TIME
        };
        Cursor cursor = mDabtabase.query(FavHelper.TABLE_NAME_MYPOST, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToLast();
            id = cursor.getInt(0);
            //cursor.close();
        }
        Log.e("gotID", "my id is "+id);
        return id;
    }

    public void deleteAll() {
        mDabtabase.delete(FavHelper.TABLE_NAME_MYPOST, null, null);
    }

    public void updateDatabase(int foreignKey, String newBitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavHelper.COLUMN_STATUS, newBitmap);
        mDabtabase.update(FavHelper.TABLE_NAME_MYPOST, contentValues, FavHelper.COLUMN_UID + "="+ foreignKey, null);//
        Log.e("UPDATE", "database updated to "+newBitmap);
    }

    public void deleteDatabase(int id) {
        mDabtabase.delete(FavHelper.TABLE_NAME_MYPOST,FavHelper.COLUMN_UID + "="+ id,null);
    }


    public class FavHelper extends SQLiteOpenHelper {

        private Context mcontext;
        private static final String DB_NAME = "mypost_db";
        private static final int DB_VERSION = 1;

        public static final String TABLE_NAME_MYPOST = "myposts";
        public static final String COLUMN_UID = "_id";

        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_VOICE = "voicenote";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_MOBILE = "mobile";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_TIME = "created_time";

        private static final String CREATE_TABLE_MYPOST = "CREATE TABLE " + TABLE_NAME_MYPOST + "(" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CAPTION + " TEXT," +
                COLUMN_VOICE + " TEXT," +
                COLUMN_IMAGE + " TEXT," +
                COLUMN_MOBILE + " TEXT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_STATUS + " TEXT," +
                COLUMN_TIME + " TEXT" +
                ");";


        public FavHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mcontext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE_MYPOST);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(" DROP TABLE " + TABLE_NAME_MYPOST + " IF EXISTS;");
                onCreate(db);
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }
}
