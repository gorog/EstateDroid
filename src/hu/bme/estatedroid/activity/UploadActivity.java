package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.City;
import hu.bme.estatedroid.model.County;
import hu.bme.estatedroid.model.Heating;
import hu.bme.estatedroid.model.Offer;
import hu.bme.estatedroid.model.Parking;
import hu.bme.estatedroid.model.Property;
import hu.bme.estatedroid.model.State;
import hu.bme.estatedroid.model.Type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

public class UploadActivity extends ParentActivity implements LocationListener {

	final Context context = this;
	AsyncTask<Void, Void, String> progressAsyncTask;
	Property[] properties;
	CharSequence[] offers;
	CharSequence[] heatings;
	CharSequence[] parkings;
	CharSequence[] states;
	CharSequence[] types;

	AutoCompleteTextView countyTextView;
	AutoCompleteTextView cityTextView;
	AutoCompleteTextView zipcodeTextView;
	ImageView countyCheckImage;
	ImageView cityCheckImage;
	ImageView zipcodeCheckImage;
	EditText streetTextView;
	EditText houseNumberTextView;
	EditText floorEditText;
	EditText roomEditText;
	CheckBox usePositionCheckBox;
	Button offerChooseButton;
	Button heatingChooseButton;
	Button parkingChooseButton;
	Button stateChooseButton;
	Button typeChooseButton;
	Button uploadButton;
	Spinner priceOption;
	Spinner rentOption;
	EditText priceEditText;
	EditText rentEditText;
	EditText placeEditText;
	EditText roomsEditText;
	TextView longitudeText;
	TextView latitudeText;
	EditText commentEditText;
	Spinner elevatorOption;

	List<County> countyList;
	List<City> cityList;
	List<Offer> offerList;
	List<Heating> heatingList;
	List<Parking> parkingList;
	List<State> stateList;
	List<Type> typeList;
	ArrayAdapter<String> cityAdapter;
	ArrayAdapter<String> zipcodeAdapter;

	String county;
	String city;
	int chosenOffer;
	int chosenHeating;
	int chosenParking;
	int chosenState;
	int chosenType;
	boolean elevator;
	int price;
	int rent;
	double lat, lng;

	Editor editor;
	SharedPreferences prefs;
	LocationManager locationManager;
	private String provider;

	public UploadActivity() {
		super("UploadActivity");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();

		countyTextView = (AutoCompleteTextView) findViewById(R.id.countyTextView);
		cityTextView = (AutoCompleteTextView) findViewById(R.id.cityTextView);
		zipcodeTextView = (AutoCompleteTextView) findViewById(R.id.zipTextView);

		countyCheckImage = (ImageView) findViewById(R.id.countyCheckImage);
		cityCheckImage = (ImageView) findViewById(R.id.cityCheckImage);
		zipcodeCheckImage = (ImageView) findViewById(R.id.zipCheckImage);

		streetTextView = (EditText) findViewById(R.id.streetTextView);
		houseNumberTextView = (EditText) findViewById(R.id.houseNumberTextView);
		floorEditText = (EditText) findViewById(R.id.floorTextView);
		roomEditText = (EditText) findViewById(R.id.roomTextView);
		usePositionCheckBox = (CheckBox) findViewById(R.id.usePositionCheckBox);
		offerChooseButton = (Button) findViewById(R.id.offerChooseButton);
		heatingChooseButton = (Button) findViewById(R.id.heatingChooseButton);
		parkingChooseButton = (Button) findViewById(R.id.parkingChooseButton);
		stateChooseButton = (Button) findViewById(R.id.stateChooseButton);
		typeChooseButton = (Button) findViewById(R.id.typeChooseButton);
		priceOption = (Spinner) findViewById(R.id.priceOption);
		rentOption = (Spinner) findViewById(R.id.rentOption);
		priceEditText = (EditText) findViewById(R.id.priceEditText);
		rentEditText = (EditText) findViewById(R.id.rentEditText);
		placeEditText = (EditText) findViewById(R.id.placeEditText);
		longitudeText = (TextView) findViewById(R.id.longitudeText);
		latitudeText = (TextView) findViewById(R.id.latitudeText);
		roomsEditText = (EditText) findViewById(R.id.roomsEditText);
		elevatorOption = (Spinner) findViewById(R.id.elevatorOption);
		commentEditText = (EditText) findViewById(R.id.commentEditText);

		uploadButton = (Button) findViewById(R.id.uploadButton);

		lat = 0;
		lng = 0;
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		provider = LocationManager.GPS_PROVIDER;
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		} else {
			latitudeText.setText(R.string.no_location);
			longitudeText.setText("");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		Dao<County, Integer> coutyDao;
		Dao<City, Integer> cityDao;
		countyList = new ArrayList<County>();
		List<String> countyListString = new ArrayList<String>();
		List<String> cityListFiltered = new ArrayList<String>();
		cityList = new ArrayList<City>();

		offerList = new ArrayList<Offer>();
		try {
			coutyDao = getHelper().getCountyDao();
			for (County p : coutyDao.queryForAll()) {
				countyList.add(p);
				countyListString.add(p.getName());
			}
			cityDao = getHelper().getCityDao();
			for (City p : cityDao.queryBuilder().query()) {
				cityList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}

		ArrayAdapter<String> countyAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				countyListString.toArray(new String[countyListString.size()]));

		countyTextView.setAdapter(countyAdapter);
		countyCheckImage.setImageResource(R.drawable.help);

		cityListFiltered = fillCity(-1);
		cityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				cityListFiltered.toArray(new String[cityListFiltered.size()]));
		cityTextView.setAdapter(cityAdapter);
		cityCheckImage.setImageResource(R.drawable.help);

		countyTextView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				countyCheckImage.setImageResource(R.drawable.cancel);
				for (County c : countyList) {
					if (c.getName().equals(s.toString())) {
						countyCheckImage.setImageResource(R.drawable.accept);
						List<String> cityListFiltered = fillCity(c.getId());
						cityAdapter = new ArrayAdapter<String>(context,
								android.R.layout.simple_dropdown_item_1line,
								cityListFiltered
										.toArray(new String[cityListFiltered
												.size()]));
						cityTextView.setAdapter(cityAdapter);
						break;
					}
				}
				if (s.toString().equals("")) {
					countyCheckImage.setImageResource(R.drawable.help);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		cityTextView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				cityCheckImage.setImageResource(R.drawable.cancel);
				for (City c : cityList) {
					if (c.getName().equals(s.toString())) {
						cityCheckImage.setImageResource(R.drawable.accept);
						List<String> zipcodeListFiltered = fillZipcode(c
								.getName());
						zipcodeAdapter = new ArrayAdapter<String>(context,
								android.R.layout.simple_dropdown_item_1line,
								zipcodeListFiltered
										.toArray(new String[zipcodeListFiltered
												.size()]));
						zipcodeTextView.setAdapter(zipcodeAdapter);
						break;
					}
				}
				if (s.toString().equals("")) {
					zipcodeCheckImage.setImageResource(R.drawable.help);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		zipcodeTextView.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				zipcodeCheckImage.setImageResource(R.drawable.cancel);
				for (City c : cityList) {
					if (c.getZipCode().equals(s.toString())) {
						zipcodeCheckImage.setImageResource(R.drawable.accept);
						break;
					}
				}
				if (s.toString().equals("")) {
					zipcodeCheckImage.setImageResource(R.drawable.help);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		countyTextView.setText(prefs.getString("u_county", ""));
		cityTextView.setText(prefs.getString("u_city", ""));
		zipcodeTextView.setText(prefs.getString("u_zipcode", ""));
		streetTextView.setText(prefs.getString("u_street", ""));
		houseNumberTextView.setText(prefs.getString("u_house_number", ""));
		floorEditText.setText(prefs.getString("u_floor", ""));
		roomEditText.setText(prefs.getString("u_room", ""));
		commentEditText.setText(prefs.getString("u_comment", ""));

		Dao<Offer, Integer> offerDao;
		List<CharSequence> offerListString = new ArrayList<CharSequence>();
		try {
			offerDao = getHelper().getOfferDao();
			for (Offer p : offerDao.queryForAll()) {
				offerListString.add(p.getType());
				offerList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		offers = offerListString.toArray(new CharSequence[offerListString
				.size()]);

		chosenOffer = prefs.getInt("u_chosenOffer", 0);

		offerChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.offer);

				builder.setSingleChoiceItems(offers, chosenOffer,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								chosenOffer = which;
								dialog.dismiss();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<Heating, Integer> heatingDao;
		heatingList = new ArrayList<Heating>();
		List<CharSequence> heatingListString = new ArrayList<CharSequence>();
		try {
			heatingDao = getHelper().getHeatingDao();
			for (Heating p : heatingDao.queryForAll()) {
				heatingListString.add(p.getName());
				heatingList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}

		heatings = heatingListString.toArray(new CharSequence[heatingListString
				.size()]);

		chosenHeating = prefs.getInt("u_chosenHeating", 0);

		heatingChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.heating);

				builder.setSingleChoiceItems(heatings, chosenHeating,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								chosenHeating = which;
								dialog.dismiss();
							}

						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<Parking, Integer> parkingDao;
		parkingList = new ArrayList<Parking>();
		List<CharSequence> parkingListString = new ArrayList<CharSequence>();
		try {
			parkingDao = getHelper().getParkingDao();
			for (Parking p : parkingDao.queryForAll()) {
				parkingListString.add(p.getName());
				parkingList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		parkings = parkingListString.toArray(new CharSequence[parkingListString
				.size()]);

		chosenParking = prefs.getInt("u_chosenParking", 0);

		parkingChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.parking);

				builder.setSingleChoiceItems(parkings, chosenParking,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								chosenParking = which;
								dialog.dismiss();
							}

						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<State, Integer> stateDao;
		stateList = new ArrayList<State>();
		List<CharSequence> stateListString = new ArrayList<CharSequence>();
		try {
			stateDao = getHelper().getStateDao();
			for (State p : stateDao.queryForAll()) {
				stateListString.add(p.getName());
				stateList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		states = stateListString.toArray(new CharSequence[stateListString
				.size()]);
		chosenState = prefs.getInt("u_chosenState", 0);

		stateChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.state);

				builder.setSingleChoiceItems(states, chosenState,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								chosenState = which;
								dialog.dismiss();
							}

						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<Type, Integer> typeDao;
		typeList = new ArrayList<Type>();
		List<CharSequence> typeListString = new ArrayList<CharSequence>();
		try {
			typeDao = getHelper().getTypeDao();
			for (Type p : typeDao.queryForAll()) {
				typeListString.add(p.getName());
				typeList.add(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		types = typeListString.toArray(new CharSequence[typeListString.size()]);
		chosenType = prefs.getInt("u_chosenType", 0);

		typeChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.type);

				builder.setSingleChoiceItems(types, chosenType,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								chosenType = which;
								dialog.dismiss();
							}

						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		priceEditText.setText(String.valueOf(prefs.getInt("u_price", 0)));
		rentEditText.setText(String.valueOf(prefs.getInt("u_rent", 0)));
		placeEditText.setText(String.valueOf(prefs.getFloat("u_place", 0.0f)));
		roomsEditText.setText(prefs.getString("u_rooms", ""));

		elevatorOption.setSelection(prefs.getInt("u_elevator", 0));
		elevatorOption
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View v,
							int position, long id) {
						if (position == 0) {
							elevator = true;
						} else {
							elevator = false;
						}

					}

					public void onNothingSelected(AdapterView<?> arg0) {
						return;
					}

				});

		uploadButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressAsyncTask = new ProgressAsyncTask();
				progressAsyncTask.execute();
			}
		});
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	public void onLocationChanged(Location location) {
		lat = location.getLatitude();
		lng = location.getLongitude();
		latitudeText.setText(String.format("%.4f%n", lat));
		longitudeText.setText(String.format("%.4f%n", lng));
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		return;
	}

	public List<String> fillCity(int countyId) {
		List<String> list = new ArrayList<String>();
		if (countyId != -1) {
			for (City c : cityList) {
				if (c.getCounty() == countyId && !list.contains(c.getName())) {
					list.add(c.getName());
				}
			}
		} else {
			for (City c : cityList) {
				if (!list.contains(c.getName())) {
					list.add(c.getName());
				}
			}
		}
		return list;
	}

	public List<String> fillZipcode(String cityName) {
		List<String> list = new ArrayList<String>();
		for (City c : cityList) {
			if (c.getName().equals(cityName)) {
				list.add(c.getZipCode());
			}
		}
		return list;
	}

	@Override
	public void onPause() {
		super.onPause();
		editor.putString("u_county", countyTextView.getText().toString());
		editor.putString("u_city", cityTextView.getText().toString());
		editor.putString("u_zipcode", zipcodeTextView.getText().toString());
		editor.putString("u_street", streetTextView.getText().toString());
		editor.putString("u_house_number", houseNumberTextView.getText()
				.toString());
		editor.putString("u_floor", floorEditText.getText().toString());
		editor.putString("u_room", roomEditText.getText().toString());
		editor.putInt("u_chosenOffer", chosenOffer);
		editor.putInt("u_chosenHeating", chosenHeating);
		editor.putInt("u_chosenParking", chosenParking);
		editor.putInt("u_chosenState", chosenState);
		editor.putInt("u_chosenType", chosenType);
		editor.putString("u_comment", commentEditText.getText().toString());

		editor.putString("u_rooms", roomsEditText.getText().toString());
		if (elevator) {
			editor.putInt("u_elevator", 0);
		} else {
			editor.putInt("u_elevator", 1);
		}

		if (!priceEditText.getText().toString().equals("")) {
			editor.putInt("u_price",
					Integer.valueOf(priceEditText.getText().toString()));
		} else {
			editor.putInt("u_price", 0);
		}
		if (!rentEditText.getText().toString().equals("")) {
			editor.putInt("u_rent",
					Integer.valueOf(rentEditText.getText().toString()));
		} else {
			editor.putInt("u_rent", 0);
		}
		if (!placeEditText.getText().toString().equals("")) {
			editor.putFloat("u_place",
					Float.valueOf(placeEditText.getText().toString()));
		} else {
			editor.putFloat("u_place", 0.0f);
		}
		editor.commit();
		locationManager.removeUpdates(this);
	}

	class ProgressAsyncTask extends AsyncTask<Void, Void, String> {
		String returnValue;

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(Void... arg0) {
			final String url = getString(R.string.base_uri)
					+ "/v1/properties.json";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			restTemplate.getMessageConverters().add(
					new FormHttpMessageConverter());
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());

			returnValue = "no_data";

			String countyId = "";
			for (County c : countyList) {
				if (c.getName().equals(countyTextView.getText().toString())) {
					countyId = String.valueOf(c.getId());
					break;
				}
			}

			if (countyId.equals("")) {
				return "errorindata";
			}

			String cityId = "";
			for (City c : cityList) {
				if (c.getName().equals(cityTextView.getText().toString())
						&& c.getZipCode().equals(
								zipcodeTextView.getText().toString())) {
					cityId = String.valueOf(c.getId());
					break;
				}
			}

			if (cityId.equals("")) {
				return "errorindata";
			}

			String offerId = "";
			for (Offer c : offerList) {
				if (c.getType().equals(offers[chosenOffer])) {
					offerId = String.valueOf(c.getId());
					break;
				}
			}

			if (offerId.equals("")) {
				return "errorindata";
			}

			String heatingId = "";
			for (Heating c : heatingList) {
				if (c.getName().equals(heatings[chosenHeating])) {
					heatingId = String.valueOf(c.getId());
					break;
				}
			}

			if (heatingId.equals("")) {
				return "errorindata";
			}

			String parkingId = "";
			for (Parking c : parkingList) {
				if (c.getName().equals(parkings[chosenParking])) {
					parkingId = String.valueOf(c.getId());
					break;
				}
			}

			if (parkingId.equals("")) {
				return "errorindata";
			}

			String stateId = "";
			for (State c : stateList) {
				if (c.getName().equals(states[chosenState])) {
					stateId = String.valueOf(c.getId());
					break;
				}
			}

			if (stateId.equals("")) {
				return "errorindata";
			}

			String typeId = "";
			for (Type c : typeList) {
				if (c.getName().equals(types[chosenType])) {
					typeId = String.valueOf(c.getId());
					break;
				}
			}

			if (typeId.equals("")) {
				return "errorindata";
			}

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("county", countyId);
			map.add("city", cityId);
			map.add("street", streetTextView.getText().toString());
			map.add("house_number", houseNumberTextView.getText().toString());
			map.add("floor", floorEditText.getText().toString());
			map.add("room", roomEditText.getText().toString());
			map.add("heating", heatingId);
			map.add("offer", offerId);
			map.add("parking", parkingId);
			map.add("place", placeEditText.getText().toString());
			map.add("state", stateId);
			map.add("type", typeId);
			map.add("price", priceEditText.getText().toString());
			map.add("rent", rentEditText.getText().toString());
			map.add("rooms", roomsEditText.getText().toString());
			if (usePositionCheckBox.isChecked()) {
				map.add("longitude", String.valueOf((int) (lng * 1000000)));
				map.add("latitude", String.valueOf((int) (lat * 1000000)));
			}
			map.add("elevator", String.valueOf(elevator));
			map.add("comment", commentEditText.getText().toString());

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
					map, requestHeaders);

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.POST, request, String.class);
				returnValue = response.getBody();
				Log.d("wtf", returnValue);
			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					Intent intent = new Intent(getBaseContext(),
							PrefsActivity.class);
					startActivity(intent);
					return "errorindata";
				}
			} catch (RestClientException e) {
				Log.d("wtf", e.getMessage());
				return "errorindata";
			}
			if (!returnValue.equals("no_data")) {
				return returnValue;
			} else {
				return "errorindata";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			dismissProgressDialog();
			if (result.equals("errorindata")) {
				Toast.makeText(context, R.string.error_in_data,
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (returnValue != "\"error\"") {
				Intent intent = new Intent(getBaseContext(),
						DetailsActivity.class);
				intent.putExtra("propertyId", Integer.parseInt(returnValue));
				startActivity(intent);
			}
		}
	}

}
