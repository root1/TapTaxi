package taptax.taptaxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RequestCab extends ActionBarActivity {
    String currentLong;
    String currentLat;
    String userId;
    String destination;
    EditText currentLocation;
    EditText mdestination;
    EditText longt;
    EditText lati;
    EditText name;
    EditText phone1;
    EditText phone2;
    Button send;
    RequestQueue mVolleyQueue;
    ProgressDialog mProgress;
    String id;
    public Float distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        currentLocation = (EditText)findViewById(R.id.currentLoc);
        mdestination = (EditText)findViewById(R.id.destination);
        longt = (EditText)findViewById(R.id.longt);
        lati = (EditText)findViewById(R.id.lat);
        name = (EditText)findViewById(R.id.name);
        phone1 = (EditText)findViewById(R.id.phone1);
        phone2 = (EditText)findViewById(R.id.phone2);
        send = (Button)findViewById(R.id.button);
        mProgress = new ProgressDialog(RequestCab.this);
        mProgress.setMessage("Please wait...");

        SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        String user_name = sharedPreferences.getString("user_name", Constants.NOTAVAILABLE);
        String user_surname = sharedPreferences.getString("user_surname", Constants.NOTAVAILABLE);
        String phone_number = sharedPreferences.getString("phone_number", Constants.NOTAVAILABLE);
         id = sharedPreferences.getString("id", Constants.NOTAVAILABLE);

        name.setText(user_name+" "+user_surname);
        phone1.setText(phone_number);
        getloc();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String scurrentLocation = currentLocation.getText().toString();
                final String destination = mdestination.getText().toString();

                final String longtitude = longt.getText().toString();
                final String latitude = lati.getText().toString();
                final String sname = name.getText().toString();
                final String sphone1 = phone1.getText().toString();
                final String sphone2 = phone2.getText().toString();


                if(scurrentLocation.equals("") || destination.equals("") || sphone1.equals("") || longtitude.equals("") || latitude.equals("")){
                    Toast.makeText(RequestCab.this, "Please fill in the missing fields", Toast.LENGTH_LONG).show();

                }else{

                    mProgress.show();
                    StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.SENDREQUEST,
                            registerAccountrSuccessListener(),
                            registerAccountErrorListener()) {
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("customer_id", id);
                            params.put("current_long", longtitude);
                            params.put("current_lat", latitude);
                            params.put("destination", destination);
                            params.put("phone1", sphone1);
                            params.put("phone2", sphone2);
                            params.put("current_location_user_input", scurrentLocation);



                            return params;

                        }
                    };
                    AppController.getInstance().addToRequestQueue(jsonObjRequest);


                }

            }
        });
    }


    public void getloc(){

        try {
            GPSService mGPSService = new GPSService(RequestCab.this);
            mGPSService.getLocation();


            if (mGPSService.isLocationAvailable == false) {

                Toast.makeText(RequestCab.this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
                return;

            } else {
                String address = "";
                // Getting location co-ordinates
                final double lat = mGPSService.getLatitude();
                final double lon = mGPSService.getLongitude();

                address = mGPSService.getLocationAddress();
                if(address.contains("Exception") || (address.contains("address"))){
                    currentLocation.setText("");
                }else{
                    currentLocation.setText(address);
                }

                lati.setText(String.valueOf(lat));
                longt.setText(String.valueOf(lon));

            }}catch (Exception e){
            currentLocation.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recent_requests, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
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
                    new AlertDialog.Builder(RequestCab.this)
                            .setTitle(status)
                            .setCancelable(false)
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which ) {

                                    //startActivity(new Intent(getApplicationContext(), Login.class));

                                }

                            })
                            .show();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RequestCab.this, RecentRequests.class));
    }
}
