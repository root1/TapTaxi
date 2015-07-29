package taptax.taptaxi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.HashMap;

public class Home extends Activity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

	private SliderLayout mDemoSlider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mDemoSlider = (SliderLayout)findViewById(R.id.slider);
		RelativeLayout recent_requests = (RelativeLayout)findViewById(R.id.recent_req);
		RelativeLayout request_taxi = (RelativeLayout)findViewById(R.id.request_taxi);
		RelativeLayout nearby_drivers = (RelativeLayout)findViewById(R.id.nearby_drivers);
		recent_requests.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Home.this, RecentRequests.class));
			}
		});
		request_taxi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Home.this, RequestCab.class));
			}
		});
		nearby_drivers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Home.this, NearByDrivers.class));
			}
		});
		HashMap<String,String> url_maps = new HashMap<String, String>();
		url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
		url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
		url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
		url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

		HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
		file_maps.put("Hannibal",R.drawable.hannibal);
		file_maps.put("Big Bang Theory", R.drawable.bigbang);
		file_maps.put("House of Cards", R.drawable.house);
		file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

		for(String name : file_maps.keySet()) {
			TextSliderView textSliderView = new TextSliderView(this);
			// initialize a SliderLayout
			textSliderView
					.description(name)
					.image(file_maps.get(name))
					.setScaleType(BaseSliderView.ScaleType.Fit)
					.setOnSliderClickListener(this);

			//add your extra information
			textSliderView.bundle(new Bundle());
			textSliderView.getBundle()
					.putString("extra", name);

			mDemoSlider.addSlider(textSliderView);
		}
		mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
		mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
		mDemoSlider.setCustomAnimation(new DescriptionAnimation());
		mDemoSlider.setDuration(4000);
		mDemoSlider.addOnPageChangeListener(this);

	}

	@Override
	public void onPageScrolled(int i, float v, int i1) {

	}

	@Override
	public void onPageSelected(int i) {

	}

	@Override
	public void onPageScrollStateChanged(int i) {

	}


	@Override
	protected void onStop() {
		// To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
		mDemoSlider.stopAutoCycle();
		super.onStop();
	}

	@Override
	public void onSliderClick(BaseSliderView slider) {
		Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
	}

}