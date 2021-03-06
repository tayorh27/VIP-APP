package net.beepinc.vip.json;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import net.beepinc.vip.AppConfig;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by tayo on 10/4/2015.
 */
public class Utils {
    private boolean isWrite = false, isAvail = false;
    private String web_url = AppConfig.web_url+"voicenotes/";
    private Context context;

    public Utils(Context context){
        this.context = context;
    }
    public Utils(){

    }

    public String checkSDCARDSIZE(){

        long size = 0;
        String getSize = "";
//32768kb;
        size = Environment.getExternalStorageDirectory().getFreeSpace();
        if(size < 200000){
            getSize = "memory_low";
        }else {
            getSize = "memory_available";
        }

        return getSize;
    }

    public String checkSDCARDSIZEFORIMAGE(){

        long size = 0;
        String getSize = "";

        size = Environment.getExternalStorageDirectory().getFreeSpace();
        if(size < 100000){
            getSize = "memory_low";
        }else {
            getSize = "memory_available";
        }

        return getSize;
    }

    public String checkSDCard(){
        String setState = "";

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.contains(state)){
            isAvail = true;
            isWrite = true;
            setState = "allow";
        }else if(Environment.MEDIA_MOUNTED_READ_ONLY.contains(state)){
            isAvail = true;
            isWrite = false;
            setState = "read";
        }else {
            isWrite = false;
            isAvail = false;
            setState = "disallow";
        }
        return setState;
    }

    public void DownloadVoiceToSDcard(String vn){
        if(checkSDCard() == "allow" && checkSDCARDSIZEFORIMAGE() == "memory_available"){
            new DownloadFileAsync(context, vn).execute(web_url+vn);
        }else {
            Toast.makeText(context, "NO SDCARD OR SDCARD SIZE LOW", Toast.LENGTH_LONG).show();
        }
    }

    class DownloadFileAsync extends AsyncTask<String, String, String>{

        String vn;
        ProgressDialog progressDialog;
        Context context;
        public DownloadFileAsync(Context context, String vn){
            this.vn = vn;
            this.context = context;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Downloading voicenote...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {

                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC+"/vip-voicenotes/");
                path.mkdirs();
                File file = new File(path, vn);

                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(file.toString());

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = inputStream.read(data)) != -1){
                    total += count;
                    publishProgress(""+(int)((total*100)/lengthOfFile));
                    outputStream.write(data,0,count);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            }catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            Toast.makeText(context, "Voicenote Downloaded!", Toast.LENGTH_LONG).show();
        }
    }
    /*

    /storage/sdcard0/download/Derin_voicenote_2016_0_5_86301.3gpDerin_voicenote_2016_0_5_86301.3gp:
     output = /storage/sdcard0/download/Derin_voicenote_2016_0_5_86301.3gp
    inputName = Derin_voicenote_2016_0_5_86301.3gp
    path = /storage/sdcard0/Music/vip-voicenotes

     */

    public void copyFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "");//inputFile
            out = new FileOutputStream(outputPath + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

}
