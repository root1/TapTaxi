package taptax.taptaxi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecentRequests extends ActionBarActivity {
    ListView mListView;
    ProgressBar mProgress;

    RequestQueue mVolleyQueue;
    EfficientAdapter mAdapter;
    String id;
    private static final String TAG = "Data";
    private List<Transactions> mDataList;
    private class Transactions {
        private String transaction_date;
        private String destination;
        private String current_location_user_input;
        private String user_name;
        private String driver_accept;
        private String car_model;
        private String driver_phone;
        private String driver_id;
        private String driver_name;
        private String current_driver_long;
        private String current_driver_lat;
        private String current_long;
        private String current_lat;
        private String current_driver_loc;
        public String getTransaction_date() {
            return transaction_date;
        }

        public void setTransaction_date(String transaction_date) {
            this.transaction_date = transaction_date;
        }


        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public String getCurrent_location_user_input() {
            return current_location_user_input;
        }

        public void setCurrent_location_user_input(String current_location_user_input) {
            this.current_location_user_input = current_location_user_input;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getDriver_phone() {
            return driver_phone;
        }

        public void setDriver_phone(String driver_phone) {
            this.driver_phone = driver_phone;
        }

        public String getCar_model() {
            return car_model;
        }

        public void setCar_model(String car_model) {
            this.car_model = car_model;
        }

        public String getDriver_accept() {
            return driver_accept;
        }

        public void setDriver_accept(String driver_accept) {
            this.driver_accept = driver_accept;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public void setDriver_id(String driver_id) {
            this.driver_id = driver_id;
        }

        public String getDriver_name() {
            return driver_name;
        }

        public void setDriver_name(String driver_name) {
            this.driver_name = driver_name;
        }

        public String getCurrent_driver_long() {
            return current_driver_long;
        }

        public void setCurrent_driver_long(String current_driver_long) {
            this.current_driver_long = current_driver_long;
        }

        public String getCurrent_driver_lat() {
            return current_driver_lat;
        }

        public void setCurrent_driver_lat(String current_driver_lat) {
            this.current_driver_lat = current_driver_lat;
        }

        public String getCurrent_driver_loc() {
            return current_driver_loc;
        }

        public void setCurrent_driver_loc(String current_driver_loc) {
            this.current_driver_loc = current_driver_loc;
        }

        public String getCurrent_long() {
            return current_long;
        }

        public void setCurrent_long(String current_long) {
            this.current_long = current_long;
        }

        public String getCurrent_lat() {
            return current_lat;
        }

        public void setCurrent_lat(String current_lat) {
            this.current_lat = current_lat;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.ListView01);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mDataList = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("user_login", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", Constants.NOTAVAILABLE);
        mVolleyQueue = Volley.newRequestQueue(this);
        callJsonArrayRequest();
        mAdapter = new EfficientAdapter(this);
        mListView.setAdapter(mAdapter);

    }
    private void callJsonArrayRequest() {
        showProgress();
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.MYTRANSACTIONS,
                createMyReqSuccessListenerMerchant(),
                createMyReqErrorListener()) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", id);
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
                                model.setCurrent_location_user_input(jsonObj.getString("current_location_user_input"));
                                model.setDestination(jsonObj.getString("destination"));
                                model.setUser_name(jsonObj.getString("user_name") + " " + jsonObj.getString("user_surname"));
                                model.setDriver_name(jsonObj.getString("driver_name"));
                                model.setCurrent_driver_lat(jsonObj.getString("current_driver_lat"));
                                model.setCurrent_driver_long(jsonObj.getString("current_driver_long"));
                                model.setCurrent_driver_loc(jsonObj.getString("current_driver_loc"));
                                model.setCurrent_lat(jsonObj.getString("current_lat"));
                                model.setCurrent_long(jsonObj.getString("current_long"));
                                model.setCar_model(jsonObj.getString("car_reg"));
                                model.setDriver_accept(jsonObj.getString("driver_accept"));
                                model.setDriver_phone(jsonObj.getString("driver_phone"));
                                model.setDriver_id(jsonObj.getString("driver_id"));
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yy/MM/dd HH:mm:ss");
                                DateTime startTime = formatter.parseDateTime((jsonObj.getString("transaction_date").replace("-", "/")));
                                DateTime  endTime =  new DateTime() ;
                                int realMinutes = Minutes.minutesBetween(startTime, endTime).getMinutes();
                                int realHours = Hours.hoursBetween(startTime, endTime).getHours();
                                int realDays = Days.daysBetween(startTime, endTime).getDays();
                                int realSeconds = Seconds.secondsBetween(startTime, endTime).getSeconds();
                                int realWeeks = Weeks.weeksBetween(startTime, endTime).getWeeks();
                                int realMonths = Months.monthsBetween(startTime, endTime).getMonths();
                                String Valu = "";

                                if(realSeconds < 60){
                                    Valu = "just now";
                                }
                                if (realMinutes==1){
                                    Valu= "1 min ";
                                }
                                if(realMinutes>= 2 && realMinutes<=59){
                                    Valu= realMinutes +" min";
                                }

                                if (realHours==1){
                                    Valu= "1 hr";
                                }

                                if(realHours>= 2 && realHours<=23){
                                    Valu= realHours +" hrs";
                                }

                                if (realDays==1){
                                    Valu= "yesterday";
                                }

                                if(realDays>= 2 && realDays<=13){
                                    Valu= realDays +" days";
                                }
                                if (realWeeks==1){
                                    Valu= "1 week";
                                }
                                if(realWeeks>= 2 && realWeeks<=3){
                                    Valu= realWeeks +" weeks";
                                }
                                if (realMonths==1){
                                    Valu= "1 month";
                                }
                                if(realMonths>= 2 && realMonths<=11){
                                    Valu= realMonths +" months";
                                }


                                model.setTransaction_date(Valu);

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
            startActivity(new Intent(RecentRequests.this, RequestCab.class));
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
        private static final int TYPE_PENDING = 0;
        private static final int TYPE_ACCEPTED = 1;
        private LayoutInflater mInflater;

        public EfficientAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getItemViewType(int position) {
            int viewType;
            if (mDataList.get(position).getDriver_id().equals("0")) {
                viewType = TYPE_PENDING;
            } else {
                viewType = TYPE_ACCEPTED;
            }
            return viewType;
        }
        @Override
        public int getViewTypeCount() {
            return 2;
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
            ViewHolderPending viewHolderPending;
            int type = getItemViewType(position);
            if (convertView == null) {
                holder = new ViewHolder();
                viewHolderPending = new ViewHolderPending();
                switch(type) {
                    case TYPE_ACCEPTED:
                        convertView = mInflater.inflate(R.layout.recent_list_item, null);
                        holder.clientName = (TextView) convertView.findViewById(R.id.name);
                        holder.time = (TextView) convertView.findViewById(R.id.time);
                        holder.locations = (TextView) convertView.findViewById(R.id.locations);
                        holder.clientImage = (ImageView) convertView.findViewById(R.id.image);
                        convertView.setTag(R.layout.recent_list_item, holder);
                        break;
                    case TYPE_PENDING:
                        convertView = mInflater.inflate(R.layout.recent_list_item_pending, null);
                        viewHolderPending.clientName = (TextView) convertView.findViewById(R.id.name);
                        viewHolderPending.time = (TextView) convertView.findViewById(R.id.time);
                        viewHolderPending.locations = (TextView) convertView.findViewById(R.id.locations);
                        viewHolderPending.clientImage = (ImageView) convertView.findViewById(R.id.image);
                        convertView.setTag(R.layout.recent_list_item_pending, viewHolderPending);
                        break;
                }
            } else {
                holder = (ViewHolder) convertView.getTag(R.layout.recent_list_item);
                viewHolderPending = (ViewHolderPending) convertView.getTag(R.layout.recent_list_item_pending);
            }
            switch(type) {
                case TYPE_ACCEPTED:
                    holder.clientName.setText(mDataList.get(position).getDriver_name());
                    holder.locations.setText(mDataList.get(position).getCurrent_location_user_input() + " - " + mDataList.get(position).getDestination());
                    holder.time.setText(mDataList.get(position).getTransaction_date());
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
                            .buildRound(String.valueOf(mDataList.get(position).getUser_name().charAt(0)), color);
                    Picasso.with(getApplicationContext())
                            .load(mDataList.get(position).getUser_name())
                            .placeholder(drawable)
                            .error(drawable)
                            .into(holder.clientImage);
                    break;
                case TYPE_PENDING:
                    viewHolderPending.clientName.setText("Driver Pending");
                    viewHolderPending.locations.setText(mDataList.get(position).getCurrent_location_user_input() + " - " + mDataList.get(position).getDestination());
                    viewHolderPending.time.setText(mDataList.get(position).getTransaction_date());
                    Typeface custom_font2 = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/RobotoSlab-Light.ttf");
                    ColorGenerator generator2 = ColorGenerator.DEFAULT;
                    int color2 = generator2.getRandomColor();
                    TextDrawable drawable2 = TextDrawable.builder()
                            .beginConfig()
                            .textColor(Color.WHITE)
                            .toUpperCase()
                            .fontSize(30)
                            .useFont(custom_font2)
                            .endConfig()
                            .buildRound(String.valueOf(mDataList.get(position).getUser_name().charAt(0)), color2);
                    Picasso.with(getApplicationContext())
                            .load(mDataList.get(position).getUser_name())
                            .placeholder(drawable2)
                            .error(drawable2)
                            .into(viewHolderPending.clientImage);
            }

            mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    Intent myIntent = new Intent(getApplicationContext(), ViewTransaction.class);
                    Bundle extras = new Bundle();
                    extras.putString("transaction_date", mDataList.get(position).getTransaction_date());
                    extras.putString("car_model", mDataList.get(position).getCar_model());
                    extras.putString("current_location_user_input", mDataList.get(position).getCurrent_location_user_input());
                    extras.putString("driver_id", mDataList.get(position).getDriver_id());
                    extras.putString("current_driver_lat", mDataList.get(position).getCurrent_driver_lat());
                    extras.putString("current_driver_loc", mDataList.get(position).getCurrent_driver_loc());
                    extras.putString("current_driver_long", mDataList.get(position).getCurrent_driver_long());
                    extras.putString("current_lat", mDataList.get(position).getCurrent_lat());
                    extras.putString("current_long", mDataList.get(position).getCurrent_long());
                    extras.putString("driver_name", mDataList.get(position).getDriver_name());
                    extras.putString("driver_phone", mDataList.get(position).getDriver_phone());
                    extras.putString("user_name", mDataList.get(position).getUser_name());
                    extras.putString("destination", mDataList.get(position).getDestination());
                    myIntent.putExtras(extras);
                    startActivity(myIntent);


                }
            });


            return convertView;

        }

        class ViewHolder {
            TextView clientName;
            TextView time;
            TextView locations;
            ImageView clientImage;

        }

        class ViewHolderPending {
            TextView clientName;
            TextView time;
            TextView locations;
            ImageView clientImage;

        }

    }
}
