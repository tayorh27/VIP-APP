package net.beepinc.vip.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Loginview;
import net.beepinc.vip.R;
import net.beepinc.vip.Signup;
import net.beepinc.vip.generalUsage.GlobalUse;
import net.beepinc.vip.network.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ResetPassword extends ActionBarActivity implements View.OnClickListener{

    TextInputLayout tu, tp;
    EditText et_p, et_cp;
    TextView signin,create;
    Button reset;

    int g_id;
    ProgressDialog progressDialog;
    VolleySingleton volleySingleton;
    RequestQueue requestQueue;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        AppConfig.ReplaceDefaultFont(ResetPassword.this, "DEFAULT", "avenir_light.ttf");

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();

        tu = (TextInputLayout) findViewById(R.id.EUsername);
        tp = (TextInputLayout) findViewById(R.id.Epassword);

        et_p = (EditText) findViewById(R.id.et_password);
        et_cp = (EditText) findViewById(R.id.et_cpassword);

        create = (TextView) findViewById(R.id.create);
        signin = (TextView) findViewById(R.id.signin);

        reset = (Button) findViewById(R.id.breset);

        reset.setOnClickListener(this);
        signin.setOnClickListener(this);
        create.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        g_id = bundle.getInt("id");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                startActivity(new Intent(ResetPassword.this, Signup.class));
                break;
            case R.id.signin:
                startActivity(new Intent(ResetPassword.this, Loginview.class));
                break;
            case R.id.breset:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        String get_p = et_p.getText().toString();
        String get_cp = et_cp.getText().toString();

        if(get_p.contentEquals("")){
            tu.setError("password cannot be empty");
            return;
        }else if(!get_p.contentEquals(get_cp)){
            Toast.makeText(ResetPassword.this, "Passwords not equal", Toast.LENGTH_LONG).show();
            return;
        }else if(get_cp.contentEquals("")){
            tp.setError("confirm password cannot be empty");
            return;
        }
        ChangePassword();
    }

    private void ChangePassword() {
        progressDialog = new ProgressDialog(ResetPassword.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("resetting password");
        progressDialog.show();
        String password = et_cp.getText().toString();
        String web_url = AppConfig.web_url+"update_files/updatePassword.php?id="+g_id+"&password="+password;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(web_url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int success = 0;
                try {
                    success = response.getInt("success");
                    if(success == 1){
                        Toast.makeText(ResetPassword.this,"password successfully changed\nLogin now.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        startActivity(new Intent(ResetPassword.this, Loginview.class));
                        finish();
                    }else{
                        Toast.makeText(ResetPassword.this, "Failed to change password\nTry again.", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                GlobalUse.handleVolleyError(error, ResetPassword.this);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
