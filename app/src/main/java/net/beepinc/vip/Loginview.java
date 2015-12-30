package net.beepinc.vip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import net.beepinc.vip.Information.favorites_information;
import net.beepinc.vip.activity.ForgotPassword;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class Loginview extends ActionBarActivity {


    Button login;
    UserLocalStore userLocalStore;
    ProgressDialog progressDialog;
    AlertDialog alertDialog;

    EditText uname, pwd;
    TextInputLayout tu, tp;
    TextView tv, forgot;
    InternetChecking isConnt;

    Boolean chks = false;
    Boolean isChks = false;
    UserLoginCheck userLoginCheck;


    public String ImageName = "";

    public static final String SERVER_ADDRESS = "http://gisanrinadetayo.comuf.com/";
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    boolean checkLog = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginview);

        AppConfig.ReplaceDefaultFont(Loginview.this, "DEFAULT", "avenir_light.ttf");

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        login = (Button) findViewById(R.id.blogin);
        uname = (EditText) findViewById(R.id.et_username);
        pwd = (EditText) findViewById(R.id.et_password);

        tu = (TextInputLayout) findViewById(R.id.EUsername);
        tp = (TextInputLayout) findViewById(R.id.Epassword);
        tv = (TextView) findViewById(R.id.create);
        forgot = (TextView) findViewById(R.id.forgot);

        isConnt = new InternetChecking(this);
        userLocalStore = new UserLocalStore(this);
        userLoginCheck = new UserLoginCheck(Loginview.this);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String get_username = uname.getText().toString();
                    String get_password = pwd.getText().toString();

                    if (get_username.contentEquals("")) {
                        tu.setError("Provide Username");
                    } else if (get_password.contentEquals("")) {
                        tp.setError("Provide Password");
                    } else {
                        chks = isConnt.isConnectedToInternet();
                        try {
                            LogUserIn(get_username, get_password);
                            //new LoggedUserIn(get_username, get_password, Loginview.this).execute();
                        } catch (Exception e) {
                            Log.d("Error5", "Error5-" + e.getMessage());
                            Toast.makeText(Loginview.this, "Error here!!!", Toast.LENGTH_LONG).show();
                        }

                    }
                } catch (Exception e) {
                    Log.d("Error6", "Error6-" + e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Loginview.this, Signup.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveDetails();
            }
        });


    }

    private void retrieveDetails() {
        String get_username = uname.getText().toString();
        if (get_username.contentEquals("")) {
            tu.setError("Provide Username");
            return;
        }
        FetchDatas(get_username);
    }

    private void FetchDatas(String username) {
        final AlertDialog alertDialog = new AlertDialog.Builder(Loginview.this).create();
        alertDialog.setTitle("VIP - Error");
        alertDialog.setMessage("Incorrect username");
        alertDialog.setButton("Try Again!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(Loginview.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Validating user");
        progressDialog.show();
        String web_url = AppConfig.web_url + "update_files/FetchSecurityAnswer.php?username=" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(web_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);
                    int gID = object.getInt("id");
                    String gSQ = object.getString("security_question");
                    String gSA = object.getString("security_answer");

                    Bundle bundle = new Bundle();
                    bundle.putInt("id", gID);
                    bundle.putString("security_question", gSQ);
                    bundle.putString("security_answer", gSA);

                    progressDialog.dismiss();

                    Intent intent = new Intent(Loginview.this, ForgotPassword.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                GlobalUse.handleVolleyError(error, Loginview.this);
                alertDialog.show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void LogUserIn(String gUser, final String gPass) {
        alertDialog = new AlertDialog.Builder(Loginview.this).create();
        alertDialog.setTitle("VIP - Error");
        alertDialog.setMessage("Incorrect username or password");
        alertDialog.setButton("Try Again!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });


        progressDialog = new ProgressDialog(Loginview.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging user in");
        progressDialog.show();
        String web_url = AppConfig.web_url + "FetchUserData.php?username=" + gUser;

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(web_url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response1) {
                try {
                    JSONObject response = response1.getJSONObject(0);
                    String getPass = response.getString("password");
                    if (getPass.contentEquals(gPass)) {
                        int id = response.getInt("id");
                        String userNm = response.getString("username");
                        String pWD = response.getString("password");
                        String mobile = response.getString("mobile");
                        String cate = response.getString("category");
                        String img = response.getString("image");
                        String sq = response.getString("security_question");
                        String sa = response.getString("security_answer");

                        User user = new User(id, userNm, mobile, img, cate, pWD, sq, sa);
                        userLocalStore.storeUserData(user);
                        userLocalStore.setUserLoggedIn(true);
                        progressDialog.dismiss();
                        Intent intent = new Intent(Loginview.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        alertDialog.show();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    Log.d("Error ", e.toString());
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                GlobalUse.handleVolleyError(error, Loginview.this);
                //alertDialog.show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}
