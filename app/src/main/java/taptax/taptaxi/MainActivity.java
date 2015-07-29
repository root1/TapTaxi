package taptax.taptaxi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;
import me.tatarka.support.os.PersistableBundle;
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
import java.util.List;


public class MainActivity extends ActionBarActivity {
    ListView mListView;
    ProgressBar mProgress;

    RequestQueue mVolleyQueue;
    EfficientAdapter mAdapter;
    JsonObjectRequest jsonObjRequest;

    private static final String TAG = "Data";
    private List<Transactions> mDataList;

    private class Transactions {

        private String transaction_date;
        private String longtitude;
        private String latitude;
        private String transaction_id;
        private String destination;
        private String current_location_user_input;
        private String user_name;
        private String user_surname;
        private String phone1;
        private String phone2;

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

        public String getLongtitude() {
            return longtitude;
        }

        public void setLongtitude(String longtitude) {
            this.longtitude = longtitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getTransaction_id() {
            return transaction_id;
        }

        public void setTransaction_id(String transaction_id) {
            this.transaction_id = transaction_id;
        }

        public String getUser_surname() {
            return user_surname;
        }

        public void setUser_surname(String user_surname) {
            this.user_surname = user_surname;
        }

        public String getPhone1() {
            return phone1;
        }

        public void setPhone1(String phone1) {
            this.phone1 = phone1;
        }

        public String getPhone2() {
            return phone2;
        }

        public void setPhone2(String phone2) {
            this.phone2 = phone2;
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
        callJsonArrayRequest();
        mAdapter = new EfficientAdapter(this);
        mListView.setAdapter(mAdapter);

        JobScheduler jobScheduler = JobScheduler.getInstance(MainActivity.this);
        PersistableBundle extras = new PersistableBundle();
        extras.putString("key", "value");
        JobInfo job = new JobInfo.Builder(0 /*jobid*/, new ComponentName(getApplicationContext(), DriverUpdateService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(10000)
                .setPersisted(true)
                .setExtras(extras)
                .build();
        jobScheduler.schedule(job);
    }
    private void callJsonArrayRequest() {
        showProgress();
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, Constants.GETTRANSACTIONS,
                createMyReqSuccessListenerMerchant(),
                createMyReqErrorListener()) {


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
                    JSONObject responses =new JSONObject(response) ;
                    //JSONObject homedata = new JSONObject(responses.getString("home"));
                    if(responses.has("details")) {

                        try {
                            JSONArray items = responses.getJSONArray("details");

                            for(int index = 0 ; index < items.length(); index++) {

                                JSONObject jsonObj = items.getJSONObject(index);
                                Transactions model = new Transactions();
                                model.setPhone1(jsonObj.getString("phone1"));
                                model.setPhone2(jsonObj.getString("phone2"));
                                model.setLatitude(jsonObj.getString("current_lat"));
                                model.setLongtitude(jsonObj.getString("current_long"));
                                model.setTransaction_id(jsonObj.getString("transaction_id"));
                                model.setUser_surname(jsonObj.getString("user_surname"));
                                model.setCurrent_location_user_input(jsonObj.getString("current_location_user_input"));
                                model.setDestination(jsonObj.getString("destination"));
                                model.setUser_name(jsonObj.getString("user_name"));
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

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mDataList.clear();
            callJsonArrayRequest();
        }
        if (id == R.id.action_my_jobs) {
           startActivity(new Intent(MainActivity.this, MyJobs.class));
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

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.incoming_list_item, null);
                holder = new ViewHolder();
                holder.clientName = (TextView) convertView.findViewById(R.id.name);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.locations = (TextView) convertView.findViewById(R.id.locations);
                holder.clientImage = (ImageView) convertView.findViewById(R.id.image);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.clientName.setText(mDataList.get(position).getUser_name() +" "+ mDataList.get(position).getUser_surname());
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


            mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {

                    Intent myIntent = new Intent(getApplicationContext(), ViewJobDetail.class);
                    Bundle extras = new Bundle();
                    extras.putString("user_name", mDataList.get(position).getUser_name() +" "+ mDataList.get(position).getUser_surname());
                    extras.putString("current_location_user_input", mDataList.get(position).getCurrent_location_user_input());
                    extras.putString("destination", mDataList.get(position).getDestination());
                    extras.putString("current_lat", mDataList.get(position).getLatitude());
                    extras.putString("current_long", mDataList.get(position).getLongtitude());
                    extras.putString("transaction_date", mDataList.get(position).getTransaction_date());
                    extras.putString("transaction_id", mDataList.get(position).getTransaction_id());
                    extras.putString("phone1", mDataList.get(position).getPhone1());
                    extras.putString("phone2", mDataList.get(position).getPhone2());
                    Toast.makeText(getApplicationContext(), mDataList.get(position).getLatitude(), Toast.LENGTH_LONG).show();
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

    }
    }
