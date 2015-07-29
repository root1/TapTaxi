package taptax.taptaxi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NearByDrivers extends ActionBarActivity {
    ListView mListView;
    ProgressBar mProgress;

    RequestQueue mVolleyQueue;
    EfficientAdapter mAdapter;
    double lat = 0;
    double lon = 0;
    private static final String TAG = "Data";
    private List<Transactions> mDataList;
    private class Transactions {
        private String driver_name;
        private String distance_away;
        private String driver_phone;

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getDistance_away() {
            return distance_away;
        }

        public void setDistance_away(String distance_away) {
            this.distance_away = distance_away;
        }

        public String getDriver_phone() {
            return driver_phone;
        }

        public void setDriver_phone(String driver_phone) {
            this.driver_phone = driver_phone;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.ListView01);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mDataList = new ArrayList<>();
        mVolleyQueue = Volley.newRequestQueue(this);
        getloc();
        callJsonArrayRequest();
        mAdapter = new EfficientAdapter(this);
        mListView.setAdapter(mAdapter);

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
    private void callJsonArrayRequest() {
        showProgress();
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.NEARBYDRIVERS,
                createMyReqSuccessListenerMerchant(),
                createMyReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", lat+"");
                params.put("long", lon+"");
                return params;
            }

        };

        mVolleyQueue.add(jsonObjRequest);
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                stopProgress();
            }
        };
    }

    private Response.Listener<String> createMyReqSuccessListenerMerchant() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                stopProgress();
                try {
                    JSONObject responses =new JSONObject(response);
                    //JSONObject homedata = new JSONObject(responses.getString("home"));
                    if(responses.has("details")) {

                        try {
                            JSONArray items = responses.getJSONArray("details");

                            for(int index = 0 ; index < items.length(); index++) {

                                JSONObject jsonObj = items.getJSONObject(index);
                                Transactions model = new Transactions();

                                model.setDriver_name(jsonObj.getString("driver_name"));
                                model.setDriver_phone(jsonObj.getString("driver_phone"));

                                DecimalFormat df = new DecimalFormat("####0.00");
                                float distance = Float.parseFloat(jsonObj.getString("dista"));
                                model.setDistance_away(df.format(distance) + " KM");
                                mDataList.add(model);

                            }
                        }catch (Exception e) {

                        }

                    }
                    mAdapter.notifyDataSetChanged();

                }catch (JSONException e) {
                    // TODO Auto-generated catch block

                }
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_new) {
            startActivity(new Intent(NearByDrivers.this, RequestCab.class));
        }
        if (id == R.id.action_refresh) {
            mDataList.clear();
            callJsonArrayRequest();

        }

        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onStop() {
        super.onStop();
        if (mProgress != null)
            mProgress.setVisibility(View.GONE);
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void stopProgress() {
        mProgress.setVisibility(View.GONE);

    }



    private class EfficientAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return mDataList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();



                        convertView = mInflater.inflate(R.layout.near_by_driver_list_item, null);
                        holder.driverName = (TextView) convertView.findViewById(R.id.name);
                        holder.driverPhone = (TextView) convertView.findViewById(R.id.driver_phone);
                        holder.distance = (TextView) convertView.findViewById(R.id.distance);
                        holder.clientImage = (ImageView) convertView.findViewById(R.id.image);
                        convertView.setTag(R.layout.near_by_driver_list_item, holder);


            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.near_by_driver_list_item);

            }

                    holder.driverName.setText(mDataList.get(position).getDriver_name());
                    holder.driverPhone.setText(mDataList.get(position).getDriver_phone());
                    holder.distance.setText(mDataList.get(position).getDistance_away());
                    Typeface custom_font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoSlab-Light.ttf");
                    ColorGenerator generator = ColorGenerator.DEFAULT;
                    int color = generator.getRandomColor();
                    TextDrawable drawable = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .toUpperCase()
                            .fontSize(30)
                            .useFont(custom_font)
                            .endConfig()
                            .buildRound(String.valueOf(mDataList.get(position).getDriver_name().charAt(0)), color);
                    Picasso.with(getApplicationContext())
                            .load(mDataList.get(position).getDriver_name())
                            .placeholder(drawable)
                            .error(drawable)
                            .into(holder.clientImage);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

//                    Intent myIntent = new Intent(getApplicationContext(), ViewTransaction.class);
//                    Bundle extras = new Bundle();
//                    extras.putString("transaction_date", mDataList.get(position).getTransaction_date());
//                    extras.putString("car_model", mDataList.get(position).getCar_model());
//                    extras.putString("current_location_user_input", mDataList.get(position).getCurrent_location_user_input());
//                    extras.putString("driver_id", mDataList.get(position).getDriver_id());
//                    extras.putString("current_driver_lat", mDataList.get(position).getCurrent_driver_lat());
//                    myIntent.putExtras(extras);
//                    startActivity(myIntent);


                }
            });


            return convertView;

        }

        class ViewHolder {
            TextView driverName;
            TextView driverPhone;
            TextView distance;
            ImageView clientImage;

        }


    }
}
