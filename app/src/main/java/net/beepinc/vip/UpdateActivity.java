package net.beepinc.vip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.activity_adapters.Security_Adapter;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UpdateActivity extends ActionBarActivity {

    Toolbar toolbar;
    Button btn;
    ImageView iv;
    EditText usern, pass, mobile,security_A;
    TextView tc;
    Spinner spin,sqs;
    InternetChecking isConn;
    boolean icheck = false;
    UserLocalStore userLocalStore;
    CatSelect catSelect;
    Security_Adapter sa;

    int RESULT_LOAD_IMAGE = 0;
    public String picturePath = "";
    public String picturePath1 = "";
    public  boolean isWrite = false, isAvail = false;
    int serverResponseCode = 0;
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;

    String setCategory = "";
    String setQuestion = "";
    private String web_url = AppConfig.web_url+"images/";

    private RequestQueue requestQueue;
    TextInputLayout tu, tm;

    //boolean access = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        volleySingleton = VolleySingleton.getInstance();
        imageLoader = volleySingleton.getImageLoader();
        requestQueue = volleySingleton.getRequestQueue();

        toolbar = (Toolbar)findViewById(R.id.update_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn = (Button)findViewById(R.id.update);
        iv = (ImageView)findViewById(R.id.update_dp);

        usern = (EditText)findViewById(R.id.update_username);
        pass = (EditText)findViewById(R.id.update_password);
        mobile = (EditText)findViewById(R.id.update_mobile);
        security_A = (EditText)findViewById(R.id.security_a);

        tc = (TextView)findViewById(R.id.txt_cat);
        tu = (TextInputLayout) findViewById(R.id.TUsername);
        tm = (TextInputLayout) findViewById(R.id.TMobile);

        spin = (Spinner)findViewById(R.id.category);
        sqs = (Spinner)findViewById(R.id.security_q);
        catSelect = new CatSelect(UpdateActivity.this);
        sa = new Security_Adapter(UpdateActivity.this);
        spin.setAdapter(catSelect);
        sqs.setAdapter(sa);

        isConn = new InternetChecking(UpdateActivity.this);
        icheck = isConn.isConnectedToInternet();
        checkSD();

        userLocalStore = new UserLocalStore(UpdateActivity.this);
        final User me_user = userLocalStore.getLoggedUser();

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/vip-images/");
        File file = new File(path, me_user.image);
        if(file.exists()){
            iv.setImageBitmap(BitmapFactory.decodeFile(file.toString()));
        }else{
            String getRealImageName = Uri.encode(me_user.image);
            imageLoader.get(web_url+getRealImageName, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    iv.setImageBitmap(response.getBitmap());
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    iv.setImageResource(R.drawable.avatar_default);
                }
            });
        }


        usern.setText(me_user.uname);
        mobile.setText(me_user.mob);
        tc.setText(me_user.cate);
        setCategory = me_user.cate;
        setQuestion = me_user.sq;
        spin.setSelection(ReturnIndex(me_user.cate),true);
        sqs.setSelection(ReturnIndexQ(me_user.sq),true);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] options = getResources().getStringArray(R.array.category);
                setCategory = options[position];
                //tc.setText(options[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sqs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] options = getResources().getStringArray(R.array.questions);
                setQuestion = options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String get_username = usern.getText().toString();
                    String get_mobile = mobile.getText().toString();
                    if (get_username.contentEquals("")) {
                        tu.setError("Provide Username");
                    }else if (get_mobile.contentEquals("")) {
                        tm.setError("Provide Password");
                    }else {
                        if (icheck) {
                            if (!picturePath.contentEquals("")) {
                                saveImage(picturePath1);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int response = uploadFile(picturePath);
                                    }
                                }).start();
                            }
                            ConfirmPassword();

                        } else {
                            Toast.makeText(UpdateActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(UpdateActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private int ReturnIndex(String gt){
        int index = 0;

        String[] array = getResources().getStringArray(R.array.category);
        for (int i = 0; i < array.length; i++){
            if(array[i].contentEquals(gt)){
                index = i;
            }
        }
        return index;
    }

    private int ReturnIndexQ(String gt){
        int index = 0;

        String[] array = getResources().getStringArray(R.array.questions);
        for (int i = 0; i < array.length; i++){
            if(array[i].contentEquals(gt)){
                index = i;
            }
        }
        return index;
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
            File file = new File(path, name);
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
            Toast.makeText(UpdateActivity.this,"NO SDCARD", Toast.LENGTH_LONG).show();
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
                //Toast.makeText(UpdateActivity.this, "uploaded", Toast.LENGTH_LONG).show();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
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
                picturePath1 = picturePath.substring(picturePath.lastIndexOf("/")+1);
            }catch (Exception e){
                Toast.makeText(UpdateActivity.this, "Error-"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
            cursor.close();
            iv.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    private void ConfirmPassword(){

        User user = userLocalStore.getLoggedUser();
        final String userPassword = user.pass;

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.actionbar_custom_image, (ViewGroup)findViewById(R.id.root));

        final EditText editText = (EditText)layout.findViewById(R.id.editText1);

        final AlertDialog ad = new AlertDialog.Builder(UpdateActivity.this).create();
        ad.setCancelable(true);
        ad.setTitle("Allow Access to Update");
        ad.setButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = editText.getText().toString();
                if(userPassword.contentEquals(password)){
                    ad.dismiss();
                    UpdateUserProfile();
                }else{
                    ad.dismiss();
                    Toast.makeText(UpdateActivity.this, "incorrect password", Toast.LENGTH_LONG).show();
                }
            }
        });
        ad.setView(layout);
        ad.show();
    }

    private void UpdateUserProfile(){

        final String uname = usern.getText().toString();
        final String password = pass.getText().toString();
        final String mob = mobile.getText().toString();
        final String cat = setCategory;
        final String sq = setQuestion;
        final String sa = security_A.getText().toString();
        final User user = userLocalStore.getLoggedUser();
        final int Id = user.id;

        String new_username = "";
        String new_password = "";
        String new_mobile = "";
        String new_category = "";
        String new_image = "";
        String new_sq = "";
        String new_sa = "";

        new_username = uname;
        new_mobile = mob;
        new_sa = sa;

        if(picturePath.contentEquals("")){
            new_image = user.image;
        }else {
            new_image = picturePath1;
        }

        if(!password.contentEquals("")) {
            new_password = password;
        }else {
            new_password = user.pass;
        }

        if(cat != user.cate) {
            new_category = cat;
        }else{
            new_category = user.cate;
        }

        if(sq != user.sq) {
            new_sq = sq;
        }else{
            new_sq = user.sq;
        }

        final ProgressDialog pdd = new ProgressDialog(UpdateActivity.this);
        pdd.setMessage("updating user profile");
        pdd.setCancelable(false);
        pdd.show();

        final String finalNew_username = new_username;
        final String finalNew_mobile = new_mobile;
        final String finalNew_image = new_image;
        final String finalNew_category = new_category;
        final String finalNew_password = new_password;
        final String finalNew_sq = new_sq;
        final String finalNew_sa = new_sa;

        final String web_url_update = AppConfig.web_url+"update.php?id="+Id+"&username="+finalNew_username+"&mobile="+finalNew_mobile+"&password="+finalNew_password+"&category="+finalNew_category+"&image="+finalNew_image+"&security_question="+finalNew_sq+"&security_answer="+finalNew_sa;

        StringRequest stringRequest = new StringRequest(web_url_update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int success=0;
                try {
                    JSONObject object = new JSONObject(response);
                    success = object.getInt("success");
                    if(success == 1){
                        userLocalStore.clearUserDatabase();
                        Toast.makeText(UpdateActivity.this,"Profile successfully updated\nLogin again.", Toast.LENGTH_LONG).show();
                        pdd.dismiss();
                        startActivity(new Intent(UpdateActivity.this, Loginview.class));
                        finish();
                    }else{
                        Toast.makeText(UpdateActivity.this, "Failed to updated profile\nTry again.", Toast.LENGTH_LONG).show();
                        pdd.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                GlobalUse.handleVolleyError(error, UpdateActivity.this);
                pdd.dismiss();
            }
        });
        requestQueue.add(stringRequest);

    }
}
