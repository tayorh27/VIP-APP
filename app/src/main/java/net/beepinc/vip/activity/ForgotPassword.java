package net.beepinc.vip.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.beepinc.vip.AppConfig;
import net.beepinc.vip.Loginview;
import net.beepinc.vip.R;
import net.beepinc.vip.Signup;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPassword extends ActionBarActivity implements View.OnClickListener{

    TextInputLayout tu, tp;
    EditText et_sq, et_sa;
    TextView signin,create;
    Button val;

    int g_id;
    String g_sq,g_sa;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        AppConfig.ReplaceDefaultFont(ForgotPassword.this, "DEFAULT", "avenir_light.ttf");

        tu = (TextInputLayout) findViewById(R.id.EUsername);
        tp = (TextInputLayout) findViewById(R.id.Epassword);

        et_sq = (EditText) findViewById(R.id.et_securityq);
        et_sa = (EditText) findViewById(R.id.et_securitya);

        create = (TextView) findViewById(R.id.create);
        signin = (TextView) findViewById(R.id.signin);

        val = (Button) findViewById(R.id.bvalidate);

        val.setOnClickListener(this);
        signin.setOnClickListener(this);
        create.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        g_id = bundle.getInt("id");
        g_sq = bundle.getString("security_question");
        g_sa = bundle.getString("security_answer");

        et_sq.setText(g_sq);
        //Toast.makeText(ForgotPassword.this, g_sa.toLowerCase(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                startActivity(new Intent(ForgotPassword.this, Signup.class));
                break;
            case R.id.signin:
                startActivity(new Intent(ForgotPassword.this, Loginview.class));
                break;
            case R.id.bvalidate:
                validateSecurityAnswer();
        }
    }

    private void validateSecurityAnswer() {
        String get_sq = et_sq.getText().toString();
        String get_sa = et_sa.getText().toString();

        if(get_sq.contentEquals("")){
            tu.setError("security question cannot be empty");
            tp.setError("");
            return;
        }else if(!get_sq.contentEquals(g_sq)){
            tu.setError("security question not valid");
            tp.setError("");
            return;
        }else if(get_sa.contentEquals("")){
            tu.setError("");
            tp.setError("security answer cannot be empty");
            return;
        }
        validateBothInputs();
    }

    private void validateBothInputs() {
        progressDialog = new ProgressDialog(ForgotPassword.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        String get_sq1 = et_sq.getText().toString();
        String get_sa1 = et_sa.getText().toString().toLowerCase();

        if(!get_sa1.contentEquals(g_sa.toLowerCase())){
            progressDialog.dismiss();
            Toast.makeText(ForgotPassword.this, "wrong security answer. Try again", Toast.LENGTH_LONG).show();
            //return;
        }else {
            Bundle bundle = new Bundle();
            bundle.putInt("id", g_id);
            Intent intent = new Intent(ForgotPassword.this, ResetPassword.class);
            intent.putExtras(bundle);
            progressDialog.dismiss();
            startActivity(intent);
            finish();
        }

    }
}
