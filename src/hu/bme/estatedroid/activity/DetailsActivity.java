package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.PropertyImageThumbLoader;
import hu.bme.estatedroid.model.Comment;
import hu.bme.estatedroid.model.Property;
import hu.bme.estatredroid.adapter.CommentAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	TextView usernameTextView;
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
	Button addFavoriteButton;

	public DetailsActivity() {
		super("DetailsActivity");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_details);

		titleTextView = (TextView) findViewById(R.id.titleTextView);
		usernameTextView = (TextView) findViewById(R.id.usernameTextView);
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
		addFavoriteButton = (Button) findViewById(R.id.addFavoriteButton);

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
			fill();
		} else {
			ProgressAsyncTask progressAsyncTask = new ProgressAsyncTask();
			progressAsyncTask.execute();
			CommentAsyncTask commentAsyncTask = new CommentAsyncTask();
			commentAsyncTask.execute();
		}
	}

	protected void fill() {
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
		if (property.getLatitude() != 0 || property.getLongitude() != 0) {
			titleTextView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String uri = String.format(Locale.ENGLISH,
							"geo:%f,%f?q=%f,%f(" + getString(R.string.app_name)
									+ ")",
							((float) property.getLatitude()) / 1000000,
							((float) property.getLongitude()) / 1000000,
							((float) property.getLatitude()) / 1000000,
							((float) property.getLongitude()) / 1000000);
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(uri));
					context.startActivity(intent);
				}
			});
		}

		commentTextView.setText(property.getComment());

		usernameTextView.setText(property.getUser());
		usernameTextView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(context, UserActivity.class);
				intent.putExtra("userNameString", property.getUser());
				startActivity(intent);
			}
		});

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
		CommentAsyncTask commentAsyncTask = new CommentAsyncTask();
		commentAsyncTask.execute();

		addFavoriteButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				FavoriteAddAsyncTask favoriteAddAsyncTask = new FavoriteAddAsyncTask(
						propertyId);
				favoriteAddAsyncTask.execute();
			}
		});
	}

	protected void fillComments(Comment[] comments) {
		List<Comment> list = new ArrayList<Comment>();
		for (Comment c : comments) {
			list.add(c);
		}
		CommentAdapter commentAdapter = new CommentAdapter(context, list);
		commentAdapter.notifyDataSetChanged();
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

	class ProgressAsyncTask extends AsyncTask<Void, Void, Integer> {

		SQLException sqlException;
		String returnValue;

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Integer doInBackground(Void... arg0) {
			final String url = getString(R.string.base_uri)
					+ "/v1/properties/{id}.json";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			returnValue = "no_data";

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					Intent intent = new Intent(getBaseContext(),
							PrefsActivity.class);
					startActivity(intent);
					sqlException = new SQLException(
							context.getString(R.string.bad_username));
					return 1;
				}
			} catch (RestClientException e) {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
			if (!returnValue.equals("no_data")) {
				return 0;
			} else {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				sqlErrorMessage(sqlException);
			} else {
				Property mProperty = (new Gson()).fromJson(returnValue,
						Property.class);
				if (mProperty != null) {
					property = mProperty;
					fill();
				}
			}
			dismissProgressDialog();
		}
	}

	class CommentAsyncTask extends AsyncTask<Void, Void, Integer> {

		SQLException sqlException;
		String returnValue;

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Integer doInBackground(Void... arg0) {
			final String url = getString(R.string.base_uri)
					+ "/v1/comments.json?property={id}";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			returnValue = "no_data";

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					Intent intent = new Intent(getBaseContext(),
							PrefsActivity.class);
					startActivity(intent);
					sqlException = new SQLException(
							context.getString(R.string.bad_username));
					return 1;
				}
			} catch (RestClientException e) {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
			if (!returnValue.equals("no_data")) {
				return 0;
			} else {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				sqlErrorMessage(sqlException);
			} else {
				Comment[] comments = (new Gson()).fromJson(returnValue,
						Comment[].class);
				if (comments != null) {
					fillComments(comments);
				}
			}
			dismissProgressDialog();
		}
	}

	class FavoriteAddAsyncTask extends AsyncTask<Void, Integer, Integer> {

		int id;
		SQLException sqlException;

		FavoriteAddAsyncTask(int id) {
			this.id = id;
		}

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Integer doInBackground(Void... urls) {

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			restTemplate.getMessageConverters().add(
					new FormHttpMessageConverter());

			String returnValue = "no_data";
			String url = getString(R.string.base_uri)
					+ "/v1/favorites.json?property={id}";

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
					map, requestHeaders);

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.POST, request, String.class, id);
				returnValue = response.getBody();
				return 0;

			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					Intent intent = new Intent(getBaseContext(),
							PrefsActivity.class);
					startActivity(intent);
					sqlException = new SQLException(
							context.getString(R.string.bad_username));
					return 1;
				}
			} catch (RestClientException e) {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
			if (!returnValue.equals("no_data")) {
				return 0;
			} else {
				sqlException = new SQLException(
						context.getString(R.string.connection_problem));
				return 1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				sqlErrorMessage(sqlException);
			}
			dismissProgressDialog();
		}
	}

}
