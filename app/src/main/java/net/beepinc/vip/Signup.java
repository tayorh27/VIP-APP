package net.beepinc.vip;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.Information.mypost_information;
import net.beepinc.vip.activity_adapters.Security_Adapter;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.helper.ParseUtils;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Signup extends ActionBarActivity {

    Spinner cat,sqs;
    CatSelect cs;
    Security_Adapter sa;
    Button s;
    ImageView iv;
    private static int RESULT_LOAD_IMAGE = 1;
    TextInputLayout tu, tm, tp, ts;

    EditText uname, mob, pwd, answer;
    TextView tv;

    public String picturePath = "";
    public String picturePath1 = "";

    LinearLayout ll;
    InternetChecking isConnt;
    Boolean chks = false;

    int serverResponseCode = 0;
    public  boolean isWrite = false, isAvail = false;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        cat = (Spinner) findViewById(R.id.category);
        sqs = (Spinner) findViewById(R.id.security_q);
        s = (Button) findViewById(R.id.signup);
        iv = (ImageView) findViewById(R.id.dp);

        tu = (TextInputLayout) findViewById(R.id.TUsername);
        tm = (TextInputLayout) findViewById(R.id.TMobile);
        tp = (TextInputLayout) findViewById(R.id.TPassword);
        ts = (TextInputLayout) findViewById(R.id.Tsecurity_a);
        tv = (TextView) findViewById(R.id.loggin);

        uname = (EditText) findViewById(R.id.username);
        mob = (EditText) findViewById(R.id.mobile);
        pwd = (EditText) findViewById(R.id.password);
        answer = (EditText) findViewById(R.id.security_a);

        ll = (LinearLayout) findViewById(R.id.linear1);

        isConnt = new InternetChecking(this);

        checkSD();
        cs = new CatSelect(Signup.this);
        cat.setAdapter(cs);

        sa = new Security_Adapter(Signup.this);
        sqs.setAdapter(sa);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Signup.this, Loginview.class));
            }
        });
        mob.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String getText = v.getText().toString();
                if(getText.contentEquals("")){
                    v.setText("+234");
                }
                return true;
            }
        });

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String get_username = uname.getText().toString();
                    String get_mobile = mob.getText().toString();
                    String get_category = cat.getSelectedItem().toString();
                    String get_question = sqs.getSelectedItem().toString();
                    String get_password = pwd.getText().toString();
                    String get_answer = answer.getText().toString();
                    if (get_username.contentEquals("")) {
                        tu.setError("Provide Username");
                    } else if (get_mobile.contentEquals("")) {
                        tm.setError("Provide Mobile Number");
                    }else if(get_answer.contentEquals("")) {
                       ts.setError("Provide security answer");
                    }else if (get_password.contentEquals("")) {
                        tp.setError("Provide Password");
                    } else {
                        tu.setError("");
                        tm.setError("");
                        ts.setError("");
                        if (picturePath.contentEquals("")) {
                            picturePath1 = "avatar_default.jpg";
                        }
                        chks = isConnt.isConnectedToInternet();
                        if (!chks) {
                            Toast.makeText(Signup.this, "No internet connection", Toast.LENGTH_LONG).show();
                        } else {
                            if(!picturePath.contentEquals("")) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int response = uploadFile(picturePath);

                                    }
                                }).start();
                            }
                            saveImage(picturePath1);
                            RegisterUser(get_username,get_mobile,get_password,get_category,picturePath1,get_question,get_answer);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage().toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
    }

    public void checkSD(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.contentEquals(state)){
            isAvail = true;
            isWrite = true;
        }else if(Environment.MEDIA_MOUNTED_READ_ONLY.contentEquals(state)){
            isAvail = true;
            isWrite = false;
        }else {
            isWrite = false;
            isAvail = false;
        }
    }

    public void saveImage(String name){
        if (isAvail && isWrite) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/vip-images/");
            //File p = Environment.getExternalStorageDirectory().getAbsoluteFile()+"";
            File file = new File(path, name);// + "/vip-images"
            path.mkdirs();

            OutputStream os;

            try {
                os = new FileOutputStream(file);
                BitmapDrawable bmd = (BitmapDrawable) iv.getDrawable();
                Bitmap bitmap = bmd.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(Signup.this,"NO SDCARD", Toast.LENGTH_LONG).show();
        }
    }

    public void saveImageToSDCARD(final String imageName){
        String serverUrl = "http://www.gisanrinadetayo.comuf.com/images/"+imageName;
        try{
            new AsyncTask<String, Void, Bitmap>(){

                @Override
                protected Bitmap doInBackground(String... params) {
                    String url = params[0];
                    Bitmap icon = null;

                    try {
                        InputStream is = new URL(url).openStream();
                        icon = BitmapFactory.decodeStream(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return icon;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    //iv.setImageBitmap(bitmap);
                    //saveImage(bitmap, imageName);
                }

            }.execute(serverUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int uploadFile(String picturePath) {
        String upLoadServerUri = AppConfig.web_url+"upload_media.php";
        String filename = picturePath;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(picturePath);
        if (!sourceFile.isFile()) {
            Log.d("uploadFile", "Source File does not exist");
            return 0;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(picturePath);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCRYPT", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", filename);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + filename + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();
            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if (serverResponseCode == 200) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            fileInputStream.close();
            dos.flush();
            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return serverResponseCode;
    }

    private void addContact(String number,String username){
        ContentValues values = new ContentValues();
        values.put(Data.RAW_CONTACT_ID,new Random().nextInt(999));
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, number);
        values.put(Phone.TYPE, Phone.TYPE_MAIN);
        values.put(Phone.LABEL, username);
        Uri dataUri = getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, values);
    }

    private void RegisterUser(final String username, final String mobile, final String password, final String category, final String image, final String sq, final String sa) {

        final ProgressDialog pdd = new ProgressDialog(Signup.this);
        pdd.setMessage("Creating user account");
        pdd.setCancelable(false);
        pdd.show();
        ParseUtils.subscribeWithEmail(username);
        String web_url_reg = AppConfig.web_url+"Register.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, web_url_reg, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success = 0;
                JSONObject resp;
                try {
                    resp = new JSONObject(response);
                    success = resp.getInt("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                    pdd.dismiss();
                    Toast.makeText(Signup.this, "Error Occurred", Toast.LENGTH_LONG).show();
                }
                if(success == 1){
                    addContact(mobile,username);
//                    GlobalUse.setList("caption", "voicenote", "image", "mobile", "username", "", "time","hide");
//                    GlobalUse.setListForMyPost("caption", "voicenote", "image", "mobile", "username", "", "time","hide");
                    Toast.makeText(Signup.this,"Account successfully created\nLogin now.", Toast.LENGTH_LONG).show();
                    pdd.dismiss();
                    startActivity(new Intent(Signup.this, Loginview.class));
                    finish();
                }else if(success == 2){
                    Toast.makeText(Signup.this, "Username already exist\nTry again.", Toast.LENGTH_LONG).show();
                    pdd.dismiss();
                }else{
                    Toast.makeText(Signup.this, "Failed to register user\nTry again.", Toast.LENGTH_LONG).show();
                    pdd.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalUse.handleVolleyError(error, Signup.this);
                pdd.dismiss();
            }
        }){
            @Override
            public String getPostBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String,String>();
                params.put("username",username);
                params.put("mobile",mobile);
                params.put("password",password);
                params.put("category",category);
                params.put("image",image);
                params.put("security_question",sq);
                params.put("security_answer",sa);
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            try {
                //int columnIndex1 = Integer.parseInt(cursor.getColumnName(Integer.parseInt(filePathColumn[0])));
                picturePath1 = picturePath.substring(picturePath.lastIndexOf("/")+1);//cursor.getString(columnIndex1);
                //Toast.makeText(Signup.this, picturePath1, Toast.LENGTH_LONG).show();

            }catch (Exception e){
                Toast.makeText(Signup.this, "Error-"+e.getMessage(), Toast.LENGTH_LONG).show();
            }


            cursor.close();
            File file = new File(picturePath);
            long fileSize = file.length();
            if(fileSize > 1000000){
                //Toast.makeText(Signup.this, "Picture size must not be greater than 500kb", Toast.LENGTH_LONG).show();
            }else{

            }

            iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            //uname.setText(picturePath);

            //Bitmap b = BitmapFactory.decodeFile(picturePath);
            //Drawable d = new BitmapDrawable(b);
            //iv.setBackground(d);
        }
    }

}
