package hu.bme.estatedroid.helper;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.Property;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PropertyAdapter extends BaseAdapter {
	private Context context;
	private Property[] propertyValue;

	public PropertyAdapter(Context context, Property[] propertyValue) {
		this.context = context;
		this.propertyValue = propertyValue;
	}

	public int getCount() {
		return propertyValue.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		if (convertView == null) {

			gridView = new View(context);

			gridView = inflater.inflate(R.layout.property_thumb, null);

			// set value into textview

			TextView cityTextView = (TextView) gridView
					.findViewById(R.id.property_name);
			Log.d("test",propertyValue[position].getCity());
			if (!propertyValue[position].getCity().equals("null")) {
				cityTextView.setText(propertyValue[position].getCity());
			} else {
				cityTextView.setText(propertyValue[position].getLongitude()+", "+propertyValue[position].getLatitude());
			}
			TextView offerTextView = (TextView) gridView
					.findViewById(R.id.property_offer);
			offerTextView.setText(propertyValue[position].getOffer());

			String price = "";

			if (propertyValue[position].getPrice() != null) {
				price += fmt(propertyValue[position].getPrice()) + " Ft";
				if (propertyValue[position].getRent() != null) {
					price += " / " + fmt(propertyValue[position].getRent())
							+ " Ft";
				}
			} else if (propertyValue[position].getRent() != null) {
				price += fmt(propertyValue[position].getRent()) + " Ft";
			}
			TextView priceTextView = (TextView) gridView
					.findViewById(R.id.property_price);
			priceTextView.setText(price);
			TextView placeTextView = (TextView) gridView
					.findViewById(R.id.property_place);
			if (propertyValue[position].getPlace() != null) {
				placeTextView.setText(fmt(propertyValue[position].getPlace())
						+ " m2");
			} else {
				placeTextView.setText("nincs megadva");
			}

			// TODO képet beállítani
			/*
			 * ImageView imageView = (ImageView) gridView
			 * .findViewById(R.id.property_value);
			 */

		} else {
			gridView = (View) convertView;
		}

		return gridView;
	}

	public static String fmt(float d) {
		if (d == (int) d)
			return String.format("%d", (int) d);
		else
			return String.format("%s", d);
	}

}
