package taptax.taptaxi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class ImageAdapter extends BaseAdapter {
	private Context context;
	private final String[] mobileValues;

	public ImageAdapter(Context context, String[] mobileValues) {
		this.context = context;
		this.mobileValues = mobileValues;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View gridView;

		if (convertView == null) {

			gridView = new View(context);
			gridView = inflater.inflate(R.layout.mobile, null);
			TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
			ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

			textView.setText(mobileValues[position]);
			String mobile = mobileValues[position];

			Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Light.ttf");
			ColorGenerator generator = ColorGenerator.DEFAULT;

			if (mobile.equals("Recent Requests")) {
				int color = generator.getRandomColor();
				TextDrawable drawable = TextDrawable.builder()
						.beginConfig()
						.textColor(Color.WHITE)
						.toUpperCase()
						.fontSize(60)
						.useFont(custom_font)
						.endConfig()
						.buildRound(String.valueOf(mobileValues[position].charAt(0)), color);

				imageView.setImageDrawable(drawable);
			} else if (mobile.equals("Make Request")) {
				int color = generator.getRandomColor();
				TextDrawable drawable = TextDrawable.builder()
						.beginConfig()
						.textColor(Color.WHITE)
						.toUpperCase()
						.fontSize(60)
						.useFont(custom_font)
						.endConfig()
						.buildRound(String.valueOf(mobileValues[position].charAt(0)), color);

				imageView.setImageDrawable(drawable);
			} else if (mobile.equals("Nearby Drivers")) {
				int color = generator.getRandomColor();
				TextDrawable drawable = TextDrawable.builder()
						.beginConfig()
						.textColor(Color.WHITE)
						.toUpperCase()
						.fontSize(60)
						.useFont(custom_font)
						.endConfig()
						.buildRound(String.valueOf(mobileValues[position].charAt(0)), color);
				imageView.setImageDrawable(drawable);
			} else {
				int color = generator.getRandomColor();
				TextDrawable drawable = TextDrawable.builder()
						.beginConfig()
						.textColor(Color.WHITE)
						.toUpperCase()
						.fontSize(60)
						.useFont(custom_font)
						.endConfig()
						.buildRound(String.valueOf(mobileValues[position].charAt(0)), color);
				imageView.setImageDrawable(drawable);
			}

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	@Override
	public int getCount() {
		return mobileValues.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
