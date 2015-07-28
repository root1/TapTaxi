package taptax.taptaxi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.style.TtsSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;


public class ViewTransaction extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        TextView name = (TextView)findViewById(R.id.name);
        TextView time = (TextView)findViewById(R.id.time);
        TextView locations = (TextView)findViewById(R.id.locations);
        TextView driver_location = (TextView)findViewById(R.id.driver_location);
        TextView user_location = (TextView)findViewById(R.id.user_location);
        TextView time_to_user = (TextView)findViewById(R.id.time_to_user);
        TextView distance = (TextView)findViewById(R.id.distance);
        TextView driver_name = (TextView)findViewById(R.id.driver_name);
        TextView driver_phone = (TextView)findViewById(R.id.driver_phone);
        TextView car_model = (TextView)findViewById(R.id.car_model);
        RelativeLayout driver_details = (RelativeLayout)findViewById(R.id.driver_details);
        RelativeLayout distance_details = (RelativeLayout)findViewById(R.id.distance_details);
        CardView driver_pending = (CardView)findViewById(R.id.driver_pending);




        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String transaction_date = extras.getString("transaction_date");
        String scar_model = extras.getString("car_model");
        String current_location_user_input = extras.getString("current_location_user_input");
        String driver_id = extras.getString("driver_id");
        String current_driver_lat = extras.getString("current_driver_lat");
        String current_driver_loc = extras.getString("current_driver_loc");
        String current_driver_long = extras.getString("current_driver_long");
        String current_lat = extras.getString("current_lat");
        String current_long = extras.getString("current_long");

        String sdriver_name = extras.getString("driver_name");
        String suser_name = extras.getString("user_name");
        String destination = extras.getString("destination");
        String sdriver_phone = extras.getString("driver_phone");


        if(driver_id.equals("0")){
            driver_pending.setVisibility(View.VISIBLE);
        }else{
            double dis=  getdist(current_driver_lat, current_driver_long, current_lat, current_long);
            DecimalFormat df = new DecimalFormat("####0.0");


            name.setText(suser_name);
            time.setText(transaction_date);
            locations.setText(current_location_user_input+" - "+destination);
            driver_location.setText("Driver Location: "+current_driver_loc);
            user_location.setText(current_location_user_input);
            time_to_user.setText("Approx: 13 min");
            distance.setText("Distance: "+df.format(dis)+" KM");
            driver_name.setText("Driver Name: "+sdriver_name);
            driver_phone.setText("Driver Phone: "+sdriver_phone);
            car_model.setText("Car Reg: "+scar_model);
            driver_details.setVisibility(View.VISIBLE);
            distance_details.setVisibility(View.VISIBLE);
        }

    }
    public double  getdist(String current_driver_lat, String current_driver_long,String current_lat,String current_long){
        Location locationA = new Location("point A");

        locationA.setLatitude(Float.parseFloat(current_driver_lat));
        locationA.setLongitude(Float.parseFloat(current_driver_long));

        Location locationB = new Location("point B");
        locationB.setLatitude(Float.parseFloat(current_lat));
        locationB.setLongitude(Float.parseFloat(current_long));

        float  distance = locationA.distanceTo(locationB);
        distance = distance/1000;

        return distance;



    }

}
