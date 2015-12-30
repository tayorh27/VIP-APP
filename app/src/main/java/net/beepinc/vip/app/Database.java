package net.beepinc.vip.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import net.beepinc.vip.model.Message;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tayo on 12/2/2015.
 */
public class Database extends Activity{

    private static ArrayList<Message> customList = new ArrayList<>();

    public void saveToLocal(String title, String message){
        try {
            FileOutputStream fos = openFileOutput(title, Context.MODE_PRIVATE);
            fos.write(message.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(Database.this,"saved",Toast.LENGTH_LONG).show();
    }

    public ArrayList<Message> getCustomList(){
        try {
            String files[];
            files = getApplicationContext().fileList();
            for (int i = 0; i < files.length; i++) {
                Message message = new Message(files[i], System.currentTimeMillis());
                customList.add(message);
            }
        }catch (Exception e){
            Log.e("error in Database",e.toString());
        }

        return customList;
    }
}
