package hu.bme.estatedroid.adapter;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.PropertyImageThumbLoader;
import hu.bme.estatedroid.model.Property;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PropertyAdapter extends BaseAdapter {
	private Context context;
	private List<Property> propertyValue;
	String username;
	String password;

	public PropertyAdapter(Context context, List<Property> propertyValue,
			String username, String password) {
		this.context = context;
		this.propertyValue = propertyValue;
		this.username = username;
		this.password = password;
	}

	public int getCount() {
		return propertyValue.size();
	}

	public Property getItem(int position) {
		return propertyValue.get(position);
	}

	public Property getItemById(int id) {
		for (Property p : propertyValue) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	public long getItemId(int position) {
		return propertyValue.get(position).getId();
	}

	public void add(Property property) {
		propertyValue.add(property);
	}

	public void remove(Property property) {
		propertyValue.remove(property);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View baseView;
		if (convertView == null && propertyValue.get(position) != null) {

			baseView = new View(context);

			baseView = inflater.inflate(R.layout.property_thumb, null);

			// set value into textview

			TextView cityTextView = (TextView) baseView
					.findViewById(R.id.property_name);
			if (!propertyValue.get(position).getCity().equals("null")) {
				cityTextView.setText(propertyValue.get(position).getCity());
			} else {
				cityTextView.setText(propertyValue.get(position).getLongitude()
						+ ", " + propertyValue.get(position).getLatitude());
			}
			TextView offerTextView = (TextView) baseView
					.findViewById(R.id.property_offer);
			offerTextView.setText(propertyValue.get(position).getOffer());

			String price = "";

			if (propertyValue.get(position).getPrice() != null) {
				price += fmt(propertyValue.get(position).getPrice()) + " Ft";
				if (propertyValue.get(position).getRent() != null) {
					price += " / " + fmt(propertyValue.get(position).getRent())
							+ " Ft";
				}
			} else if (propertyValue.get(position).getRent() != null) {
				price += fmt(propertyValue.get(position).getRent()) + " Ft";
			}
			TextView priceTextView = (TextView) baseView
					.findViewById(R.id.property_price);
			priceTextView.setText(price);
			TextView placeTextView = (TextView) baseView
					.findViewById(R.id.property_place);
			if (propertyValue.get(position).getPlace() != null) {
				placeTextView.setText(fmt(propertyValue.get(position)
						.getPlace()) + " m2");
			} else {
				placeTextView.setText("nincs megadva");
			}

			// TODO képet beállítani
			ImageView imageView = (ImageView) baseView.findViewById(R.id.icon);
			PropertyImageThumbLoader pitl = new PropertyImageThumbLoader(
					context, imageView, propertyValue.get(position).getId(),
					username, password);
			imageView = pitl.getImageView();

		} else {
			baseView = (View) convertView;
		}

		return baseView;
	}

	public static String fmt(float d) {
		if (d == (int) d)
			return String.format("%d", (int) d);
		else
			return String.format("%s", d);
	}

	public static Bitmap getBitmapFromURL(String src) {
		try {
			Log.e("src", src);
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			Log.e("Bitmap", "returned");
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Exception", e.getMessage());
			return null;
		}
	}
}
