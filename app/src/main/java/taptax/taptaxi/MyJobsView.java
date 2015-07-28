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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class MyJobsView extends ActionBarActivity {
    String current_lat;
    String current_long;
     double lat = 0;
     double lon = 0;
    double distanceKM;
    RequestQueue mVolleyQueue;
    ProgressDialog mProgress;
    JsonObjectRequest jsonObjRequest;
    String address = "";
    String transaction_id;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_job_view);
        mVolleyQueue = Volley.newRequestQueue(this);
        mProgress = new ProgressDialog(MyJobsView.this);
        mProgress.setMessage("Please wait...");


        SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", Constants.NOTAVAILABLE);

        TextView name = (TextView)findViewById(R.id.name);
        TextView time = (TextView)findViewById(R.id.time);
        TextView locations = (TextView)findViewById(R.id.locations);
        TextView driver_location = (TextView)findViewById(R.id.driver_location);
        TextView user_location = (TextView)findViewById(R.id.user_location);
        TextView time_to_user = (TextView)findViewById(R.id.time_to_user);
        TextView distance = (TextView)findViewById(R.id.distance);

        TextView user_location2 = (TextView)findViewById(R.id.user_location2);
        TextView user_location_destination = (TextView)findViewById(R.id.driver_name);
        TextView time_to_destination = (TextView)findViewById(R.id.time_to_destination);

        TextView distance2 = (TextView)findViewById(R.id.distance2);
        TextView phone1 = (TextView)findViewById(R.id.phone1);
        TextView phone2 = (TextView)findViewById(R.id.phone2);
        Button accept = (Button)findViewById(R.id.accept);




        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
         transaction_id = extras.getString("transaction_id");
        String user_name = extras.getString("user_name");
        String current_location_user_input = extras.getString("current_location_user_input");
        String destination = extras.getString("destination");
        current_lat = extras.getString("current_lat");
        current_long = extras.getString("current_long");
        String transaction_date = extras.getString("transaction_date");
        String sphone1 = extras.getString("phone1");
        String sphone2 = extras.getString("phone2");

        getloc();
        getdist();

        name.setText(user_name);
        time.setText(transaction_date);
        locations.setText(current_location_user_input+" - "+destination);
        driver_location.setText(address);
        user_location.setText(current_location_user_input);
        time_to_user.setText("Approx: 10 min");
        user_location2.setText(current_location_user_input);
        user_location_destination.setText(destination);
        time_to_destination.setText("Approx: 20 im");
        distance2.setText("Distance: N?A");
        phone1.setText(sphone1);
        phone2.setText(sphone2);


        DecimalFormat df = new DecimalFormat("####0.0");
        distance.setText("Distance: "+df.format(distanceKM)+" KM");

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.show();
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.ACCEPTCLIENT,
                        registerAccountrSuccessListener(),
                        registerAccountErrorListener()) {
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("transaction_id", transaction_id);
                        params.put("transaction_state", "accepted");
                        params.put("driver_accept", "true");
                        params.put("driver_id", id);
                        params.put("current_driver_long", lon+"");
                        params.put("current_driver_lat", lat+"");
                        params.put("current_driver_loc", address);
                        return params;

                    }
                };
                mVolleyQueue.add(jsonObjRequest);
            }
        });


    }
    public void getdist(){
        Location locationA = new Location("point A");

        locationA.setLatitude(Float.parseFloat(current_lat));
        locationA.setLongitude(Float.parseFloat(current_long));

        Location locationB = new Location("point B");
        locationB.setLatitude(lat);
        locationB.setLongitude(lon);

       float  distance = locationA.distanceTo(locationB);
       distanceKM = distance/1000;


    }
    public void getloc(){

        try {
            GPSService mGPSService = new GPSService(MyJobsView.this);
            mGPSService.getLocation();


            if (mGPSService.isLocationAvailable == false) {

                Toast.makeText(MyJobsView.this, "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
                return;

            } else {

                // Getting location co-ordinates
                 lat = mGPSService.getLatitude();
                 lon = mGPSService.getLongitude();

                address = mGPSService.getLocationAddress();
                if(address.contains("Exception") || (address.contains("address"))){
                    address  = Constants.NOTAVAILABLE;
                }else{
                    //currentLocation.setText(address);
                }

               // lati.setText(String.valueOf(lat));
                //longt.setText(String.valueOf(lon));

            }}catch (Exception e){
           // currentLocation.setText("");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_job_detail, menu);
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
                Log.e("response", response);
                try {
                    JSONObject responses =new JSONObject(response) ;

                    String status = responses.getString("success");
                    String message = responses.getString("message");
                    new AlertDialog.Builder(MyJobsView.this)
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
}
