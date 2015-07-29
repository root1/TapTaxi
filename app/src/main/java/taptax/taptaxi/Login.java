package taptax.taptaxi;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;



/**
 * Created by neokree on 12/12/14.
 */
public class Login extends ActionBarActivity {

    Toolbar toolbar;
    RequestQueue mVolleyQueue;
    JsonObjectRequest jsonObjRequest;
    ProgressDialog mProgress;

    TextView mCreate;
    EditText mPhoneNumber, mPassword;
    Button mContinue;
    String sPhoneNumber = null, sPassword = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(toolbar);

        mCreate = (TextView) findViewById(R.id.create);
        mPhoneNumber = (EditText) findViewById(R.id.editText1);
        mPassword = (EditText) findViewById(R.id.editText2);
        mContinue = (Button) findViewById(R.id.btncontinue);
        mVolleyQueue = Volley.newRequestQueue(this);
        mProgress = new ProgressDialog(Login.this);
        mProgress.setMessage("Please wait...");
        SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        String user_type = sharedPreferences.getString("user_type", Constants.NOTAVAILABLE);

        if(!user_type.equals(Constants.NOTAVAILABLE)){


            if(user_type.equals(Constants.USERTYPEDRIVER)){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            if(user_type.equals(Constants.USERTYPEREGULAR)){
                startActivity(new Intent(getApplicationContext(), RecentRequests.class));
            }
            finish();

        }
        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sPhoneNumber = mPhoneNumber.getText().toString();
                sPassword = mPassword.getText().toString();
                if(sPhoneNumber.equals("") ||sPassword.equals("")){
                    Toast.makeText(Login.this, "Please fill in the missing fields", Toast.LENGTH_LONG).show();
                }else{
                    mProgress.show();

                    StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.LOGIN,
                            loginSuccessListener(),
                            loginErrorListener()) {
			          protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
			              Map<String, String> params = new HashMap<String, String>();
			              params.put("user_phone_number", sPhoneNumber);
			              params.put("user_password", sPassword);
			              return params;
			              }
                    };
                    mVolleyQueue.add(jsonObjRequest);
                }

            }
        });
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterAccount.class));
            }
        });
    }

    private Response.ErrorListener loginErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (mProgress.isShowing())
                    mProgress.dismiss();
                new AlertDialog.Builder(Login.this)
                        .setCancelable(false)
                        .setMessage("Network unavailable. Please try again later")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which ) {

                            }

                        })
                        .show();
            }
        };
    }

    private Response.Listener<String> loginSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject responses =new JSONObject(response) ;
                    String status = responses.getString("user_phone_number");
                    if(status.equals("Error signing in")){
                        String message = responses.getString("message");
                        if (mProgress.isShowing())
                            mProgress.dismiss();
                        new AlertDialog.Builder(Login.this)
                                .setTitle(status)
                                .setCancelable(false)
                                .setMessage(message)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which ) {

                                    }

                                })
                                .show();
                    }else{

                        String name = responses.getString("user_name");
                        String surname = responses.getString("user_surname");
                        String phone_number = responses.getString("user_phone_number");
                        String email = responses.getString("user_email");
                        String password = responses.getString("user_password");
                        String id = responses.getString("user_id");
                        String user_type = responses.getString("user_type");



                        SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_name", name);
                        editor.putString("user_surname", surname);
                        editor.putString("phone_number", phone_number);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putString("id", id);
                        editor.putString("user_type", user_type);

                        editor.commit();

                        if (mProgress.isShowing())
                            mProgress.dismiss();


                        if(user_type.equals(Constants.USERTYPEDRIVER)){
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        if(user_type.equals(Constants.USERTYPEREGULAR)){
                            startActivity(new Intent(getApplicationContext(), Home.class));
                        }

                        finish();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
