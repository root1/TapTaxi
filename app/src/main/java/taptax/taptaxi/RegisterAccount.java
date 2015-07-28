package taptax.taptaxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class RegisterAccount extends ActionBarActivity {
    Toolbar toolbar;
    RequestQueue mVolleyQueue;
    ProgressDialog mProgress;
    JsonObjectRequest jsonObjRequest;
    EditText mName, mSurname, mEmail, mPhoneNumber, mPassword, vPassword;
    String sName = null, sSurname = null, sEmail = null, sPhoneNumber = null, sPassword = null, sVPassword = null;
    Button mSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);
//        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mVolleyQueue = Volley.newRequestQueue(this);
        mName = (EditText) findViewById(R.id.name);
        mProgress = new ProgressDialog(RegisterAccount.this);
        mProgress.setMessage("Please wait...");
        mSurname = (EditText) findViewById(R.id.surname);
        mEmail = (EditText) findViewById(R.id.email);
        mPhoneNumber = (EditText) findViewById(R.id.phone);
        mPassword = (EditText) findViewById(R.id.create_pass);
        vPassword = (EditText) findViewById(R.id.very_pass);
        mSubmit = (Button) findViewById(R.id.submit);

        mSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sName = mName.getText().toString();
                sSurname = mSurname.getText().toString();
                sEmail = mEmail.getText().toString();
                sPhoneNumber = mPhoneNumber.getText().toString();
                sPassword = mPassword.getText().toString();
                sVPassword = vPassword.getText().toString();
                if(mName.getText().toString().equals("") || mSurname.getText().toString().equals("") || mEmail.getText().toString().equals("")  ||
                        mPhoneNumber.getText().toString().equals("")  || mPassword.getText().toString().equals("")  || vPassword.getText().toString().equals("") ){
                    Toast.makeText(RegisterAccount.this, "Please fill in the missing fields", Toast.LENGTH_LONG).show();

                }else{
                    if(sPassword.equals(sVPassword)){
                        mProgress.show();
                        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.REGISTERACCOUNT,
                                registerAccountrSuccessListener(),
                                registerAccountErrorListener()) {
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                Map<String, String> params = new HashMap<>();
                                params.put("hope_name", sName);
                                params.put("hope_surname", sSurname);
                                params.put("hope_phone_number", sPhoneNumber);
                                params.put("hope_email", sEmail);
                                params.put("hope_password", sPassword);
                                return params;

                        }
                    };
                        mVolleyQueue.add(jsonObjRequest);

                    }else{
                        Toast.makeText(RegisterAccount.this, "Passwords do not match, verify and try again", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
//            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);

    }


    private Response.ErrorListener registerAccountErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (mProgress.isShowing())
                    mProgress.dismiss();
            }
        };
    }
    private Response.Listener<String> registerAccountrSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (mProgress.isShowing())
                    mProgress.dismiss();

                try {
                    JSONObject responses =new JSONObject(response) ;
                    String status = responses.getString("success");
                    String message = responses.getString("message");
                    new AlertDialog.Builder(RegisterAccount.this)
                            .setTitle(status)
                            .setCancelable(false)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which ) {

                                    startActivity(new Intent(getApplicationContext(), Login.class));

                                }

                            })
                            .show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


}
