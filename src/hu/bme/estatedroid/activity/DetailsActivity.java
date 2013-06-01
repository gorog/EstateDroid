package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.CommentAdapter;
import hu.bme.estatedroid.helper.PropertyImageThumbLoader;
import hu.bme.estatedroid.model.Comment;
import hu.bme.estatedroid.model.Property;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class DetailsActivity extends ParentActivity {

	final Context context = this;
	int propertyId;
	Property property;

	TextView titleTextView;
	TextView offerTextView;
	TextView typeTextView;
	TextView priceTextView;
	TextView rentTextView;
	TextView placeTextView;
	TextView stateTextView;
	TextView roomsTextView;
	TextView heatingTextView;
	TextView elevatorTextView;
	TextView parkingTextView;
	TextView commentTextView;
	TextView timestampTextView;
	LinearLayout commentList;
	static ImageView mainImageView;
	LinearLayout imageList;
	static PropertyImageThumbLoader thumbLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		typeTextView = (TextView) findViewById(R.id.typeTextView);
		priceTextView = (TextView) findViewById(R.id.priceTextView);
		rentTextView = (TextView) findViewById(R.id.rentTextView);
		placeTextView = (TextView) findViewById(R.id.placeTextView);
		stateTextView = (TextView) findViewById(R.id.stateTextView);
		roomsTextView = (TextView) findViewById(R.id.roomsTextView);
		heatingTextView = (TextView) findViewById(R.id.heatingTextView);
		elevatorTextView = (TextView) findViewById(R.id.elevatorTextView);
		parkingTextView = (TextView) findViewById(R.id.parkingTextView);
		commentTextView = (TextView) findViewById(R.id.commentTextView);
		timestampTextView = (TextView) findViewById(R.id.timestampTextView);
		mainImageView = (ImageView) findViewById(R.id.mainImageView);
		imageList = (LinearLayout) findViewById(R.id.imageList);
		commentList = (LinearLayout) findViewById(R.id.commentList);

		Intent intent = getIntent();
		propertyId = intent.getIntExtra("propertyId", 0);

	}

	@Override
	protected void onResume() {
		super.onResume();
		List<Property> list = new ArrayList<Property>();
		try {
			Dao<Property, Integer> dao = getHelper().getPropertyDao();
			QueryBuilder<Property, Integer> queryBuilder = dao.queryBuilder();
			queryBuilder.where().eq("id", propertyId);
			PreparedQuery<Property> preparedQuery = queryBuilder.prepare();

			list = dao.query(preparedQuery);
		} catch (SQLException e) {

		}
		if (list.size() > 0) {
			property = list.get(0);
			fill(property);
		} else {
			ProgressAsyncTask progressAsyncTask = new ProgressAsyncTask();
			progressAsyncTask.execute();
			CommentAsyncTask commentAsyncTask = new CommentAsyncTask();
			commentAsyncTask.execute();
		}
	}

	protected void fill(Property property) {
		String offer = Character.toUpperCase(property.getOffer().charAt(0))
				+ property.getOffer().substring(1);
		String title = offer + ",";
		if (!property.getCity().equals("")
				&& !property.getCity().equals("NULL")) {
			title += " " + property.getCity();
		}
		if (!property.getStreet().equals("")
				&& !property.getStreet().equals("NULL")) {
			title += " " + property.getStreet();
		}
		if (!property.getHouse_number().equals("")
				&& !property.getHouse_number().equals("NULL")) {
			title += " " + property.getHouse_number() + ".";
		}
		if (!property.getFloor().equals("")
				&& !property.getFloor().equals("NULL")) {
			title += " " + property.getFloor();
		}
		if (!property.getRoom().equals("")
				&& !property.getRoom().equals("NULL")) {
			title += "/" + property.getRoom();
		}
		titleTextView.setText(title);

		commentTextView.setText(property.getComment());

		typeTextView.setText(property.getType());
		if (property.getPrice() != null) {
			priceTextView.setText(fmt(property.getPrice()));
		}
		if (property.getRent() != null) {
			rentTextView.setText(fmt(property.getRent()));
		}
		if (property.getPlace() != null) {
			placeTextView.setText(fmt(property.getPlace()));
		}
		stateTextView.setText(property.getState());
		roomsTextView.setText(property.getRooms());
		heatingTextView.setText(property.getHeating());
		if (property.isElevator()) {
			elevatorTextView.setText(R.string.has_elevator);
		} else {
			elevatorTextView.setText(R.string.doesnt_have_elevator);
		}
		parkingTextView.setText(property.getParking());
		timestampTextView.setText(property.getTimestamp());

		thumbLoader = new PropertyImageThumbLoader(context, mainImageView,
				propertyId, username, password, imageList);
		mainImageView = thumbLoader.getImageView();
		imageList = thumbLoader.getImageList();
		/*
		 * for (int i = 0; i < thumbLoader.getCount(); i++) { ImageView
		 * mImageView = new ImageView(context); mImageView =
		 * thumbLoader.getImageView(i); imageList.addView(mImageView); }
		 */
	}

	protected void fillComments(Comment[] comments) {
		List<Comment> list = new ArrayList<Comment>();
		for (Comment c : comments) {
			list.add(c);
		}
		CommentAdapter commentAdapter = new CommentAdapter(context, list);
		for (int i = 0; i < commentAdapter.getCount(); i++) {
			commentList.addView(commentAdapter.getView(i, null, null));
		}
	}

	public static String fmt(float d) {
		if (d == (int) d)
			return String.format("%d", (int) d);
		else
			return String.format("%s", d);
	}

	class ProgressAsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			final String url = getString(R.string.base_uri)
					+ "/v1/properties/{id}.json";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "";

			try {
				// Make the network request
				Log.d(TAG, url);
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					// TODO kezelni, ha nem sikerült az authentikáció

				}
				// TODO többi hibaüzenetre is kezelni, pl nem megy a
				// szolgáltatás
			}

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {

			Property mProperty = (new Gson()).fromJson(result, Property.class);
			dismissProgressDialog();
			if (mProperty != null) {
				fill(mProperty);
			}
		}
	}

	class CommentAsyncTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			final String url = getString(R.string.base_uri)
					+ "/v1/comments.json?property={id}";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "";

			try {
				// Make the network request
				Log.d(TAG, url);
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					// TODO kezelni, ha nem sikerült az authentikáció

				}
				// TODO többi hibaüzenetre is kezelni, pl nem megy a
				// szolgáltatás
			}

			return returnValue;
		}

		@Override
		protected void onPostExecute(String result) {

			Comment[] comments = (new Gson()).fromJson(result, Comment[].class);
			dismissProgressDialog();
			Log.d("ez", result);
			if (comments != null) {
				fillComments(comments);
			}
		}
	}

}
