package taptax.taptaxi;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;

public class GridViewActivity extends Activity {

	GridView gridView;

	static final String[] MOBILE_OS = new String[] { "Recent Requests", "Make Request",
			"Nearby Drivers", "Blackberry" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		gridView = (GridView) findViewById(R.id.gridView1);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View footerView = layoutInflater.inflate(R.layout.gridview_header, null);
		gridView.addView(footerView);
		gridView.setAdapter(new ImageAdapter(this, MOBILE_OS));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Toast.makeText(
						getApplicationContext(),
						((TextView) v.findViewById(R.id.grid_item_label))
								.getText(), Toast.LENGTH_SHORT).show();

			}
		});

	}

}