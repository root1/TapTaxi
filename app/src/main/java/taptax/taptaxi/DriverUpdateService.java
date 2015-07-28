package taptax.taptaxi;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
/**
 * Created by Hopewell Mutanda on 7/24/2015.
 */
public class DriverUpdateService extends JobService{
    RequestQueue mVolleyQueue;
    double lat = 0;
    double lon = 0;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, jobParameters));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }

    private Handler mJobHandler = new Handler( new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msg ) {
            mVolleyQueue = Volley.newRequestQueue(getApplicationContext());
            SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
           final String id = sharedPreferences.getString("id", Constants.NOTAVAILABLE);
            String user_type = sharedPreferences.getString("user_type", Constants.NOTAVAILABLE);

            if(user_type.equals(Constants.USERTYPEDRIVER)){
                getloc();
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.UPDATEDRIVERLOCATION,
                        registerAccountrSuccessListener(),
                        registerAccountErrorListener()) {
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("driver_id", id);
                        params.put("current_long", lon+"");
                        params.put("current_lat", lat+"");

                        return params;

                    }
                };
                mVolleyQueue.add(jsonObjRequest);
            }
            Toast.makeText( getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT ).show();
            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }

    } );

    public void getloc(){

        try {
            GPSService mGPSService = new GPSService(getApplicationContext());
            mGPSService.getLocation();


            if (mGPSService.isLocationAvailable == false) {

                Toast.makeText(getApplicationContext(), "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();
                return;

            } else {

                // Getting location co-ordinates
                lat = mGPSService.getLatitude();
                lon = mGPSService.getLongitude();


            }}catch (Exception e){
            // currentLocation.setText("");
        }
    }
    
    private Response.ErrorListener registerAccountErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

            }
        };
    }
    private Response.Listener<String> registerAccountrSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("response", response);

            }
        };
    }
}
