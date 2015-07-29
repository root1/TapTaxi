package taptax.taptaxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by Hopewell Mutanda on 7/24/2015.
 */
public class DriverUpdateService extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, jobParameters));
        new MyTask(this).execute(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        return false;
    }


    private class MyTask extends AsyncTask<JobParameters, Void, JobParameters> {
        DriverUpdateService myService;
        String id;
        String user_type;
        RequestQueue mVolleyQueue;
        double lat = 0;
        double lon = 0;
        Looper looper;
        MyTask(DriverUpdateService myService) {
            this.myService = myService;
            mVolleyQueue = Volley.newRequestQueue(getApplicationContext());
            SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
            id = sharedPreferences.getString("id", Constants.NOTAVAILABLE);
            user_type = sharedPreferences.getString("user_type", Constants.NOTAVAILABLE);
            getloc();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JobParameters doInBackground(JobParameters... params) {
//            looper.getThread();
            String result = sendRequest();
           Toast.makeText(getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
            return params[0];
        }

        @Override
        protected void onPostExecute(JobParameters jobParameters) {
            myService.jobFinished(jobParameters, false);
        }

        public String sendRequest() {
            String res = null;
            if (user_type.equals(Constants.USERTYPEDRIVER)) {

                RequestFuture<String> requestFuture = RequestFuture.newFuture();
                StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.UPDATEDRIVERLOCATION,
                        requestFuture,
                        requestFuture) {
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("driver_id", id);
                        params.put("current_long", lon + "");
                        params.put("current_lat", lat + "");

                        return params;

                    }
                };
                mVolleyQueue.add(jsonObjRequest);
                try {

                    res = requestFuture.get(10000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
            return res;
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



                }
            };
        }

        public void getloc() {

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


                }
            } catch (Exception e) {
                // currentLocation.setText("");
            }
        }
    }


}
