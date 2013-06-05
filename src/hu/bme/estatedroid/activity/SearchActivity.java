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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class SearchActivity extends ParentActivity {

	final Context context = this;
	AsyncTask<Void, Void, Integer> progressAsyncTask;
	Property[] properties;
	CharSequence[] offers;
	CharSequence[] heatings;
	CharSequence[] parkings;
	CharSequence[] states;
	CharSequence[] types;

	AutoCompleteTextView countyTextView;
	AutoCompleteTextView cityTextView;
	Button offerChooseButton;
	Button heatingChooseButton;
	Button parkingChooseButton;
	Button stateChooseButton;
	Button typeChooseButton;
	Button searchButton;
	Spinner priceOption;
	Spinner rentOption;
	EditText priceEditText;
	EditText rentEditText;

	String county;
	String city;
	int chosenOffer;
	boolean[] chosenHeatings;
	boolean[] chosenParkings;
	boolean[] chosenStates;
	boolean[] chosenTypes;
	int priceChosenOption;
	int rentChosenOption;
	Editor editor;
	SharedPreferences prefs;

	public SearchActivity() {
		super("SearchActivity");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();

		countyTextView = (AutoCompleteTextView) findViewById(R.id.countyTextView);
		cityTextView = (AutoCompleteTextView) findViewById(R.id.cityTextView);
		offerChooseButton = (Button) findViewById(R.id.offerChooseButton);
		heatingChooseButton = (Button) findViewById(R.id.heatingChooseButton);
		parkingChooseButton = (Button) findViewById(R.id.parkingChooseButton);
		stateChooseButton = (Button) findViewById(R.id.stateChooseButton);
		typeChooseButton = (Button) findViewById(R.id.typeChooseButton);
		priceOption = (Spinner) findViewById(R.id.priceOption);
		rentOption = (Spinner) findViewById(R.id.rentOption);
		priceEditText = (EditText) findViewById(R.id.priceEditText);
		rentEditText = (EditText) findViewById(R.id.rentEditText);

		searchButton = (Button) findViewById(R.id.searchButton);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Gson gson = new Gson();

		Dao<County, Integer> coutyDao;
		Dao<City, Integer> cityDao;
		List<String> countylist = new ArrayList<String>();
		List<String> citylist = new ArrayList<String>();
		try {
			coutyDao = getHelper().getCountyDao();
			for (County p : coutyDao.queryForAll()) {
				countylist.add(p.getName());
			}
			cityDao = getHelper().getCityDao();
			for (City p : cityDao.queryBuilder().distinct()
					.selectColumns("name").query()) {
				citylist.add(p.getName());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}

		ArrayAdapter<String> countyAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				countylist.toArray(new String[countylist.size()]));

		countyTextView.setAdapter(countyAdapter);
		ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				citylist.toArray(new String[citylist.size()]));

		cityTextView.setAdapter(cityAdapter);

		countyTextView.setText(prefs.getString("county", ""));
		cityTextView.setText(prefs.getString("city", ""));

		Dao<Offer, Integer> offerDao;
		List<CharSequence> offerList = new ArrayList<CharSequence>();
		try {
			offerDao = getHelper().getOfferDao();
			for (Offer p : offerDao.queryForAll()) {
				offerList.add(p.getType());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		offers = offerList.toArray(new CharSequence[offerList.size()]);

		chosenOffer = prefs.getInt("chosenOffer", 0);

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
		List<CharSequence> heatingList = new ArrayList<CharSequence>();
		try {
			heatingDao = getHelper().getHeatingDao();
			for (Heating p : heatingDao.queryForAll()) {
				heatingList.add(p.getName());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}

		heatings = heatingList.toArray(new CharSequence[heatingList.size()]);

		chosenHeatings = new boolean[heatingList.size()];
		for (int i = 0; i < chosenHeatings.length; i++) {
			chosenHeatings[i] = true;
		}

		String chosenHeatingsSet = prefs.getString("chosenHeatings",
				gson.toJson(chosenHeatings));

		chosenHeatings = gson.fromJson(chosenHeatingsSet, boolean[].class);

		if (chosenHeatings.length != heatings.length) {
			chosenHeatings = new boolean[heatingList.size()];
			for (int i = 0; i < chosenHeatings.length; i++) {
				chosenHeatings[i] = true;
			}
		}

		heatingChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.heating);

				builder.setMultiChoiceItems(heatings, chosenHeatings,
						new DialogInterface.OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								chosenHeatings[which] = isChecked;
							}

						});
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<Parking, Integer> parkingDao;
		List<CharSequence> parkingList = new ArrayList<CharSequence>();
		try {
			parkingDao = getHelper().getParkingDao();
			for (Parking p : parkingDao.queryForAll()) {
				parkingList.add(p.getName());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		parkings = parkingList.toArray(new CharSequence[parkingList.size()]);

		chosenParkings = new boolean[parkingList.size()];
		for (int i = 0; i < chosenParkings.length; i++) {
			chosenParkings[i] = true;
		}

		String chosenParkingsSet = prefs.getString("chosenParkings",
				gson.toJson(chosenParkings));

		chosenParkings = gson.fromJson(chosenParkingsSet, boolean[].class);

		if (chosenParkings.length != parkings.length) {
			chosenParkings = new boolean[parkingList.size()];
			for (int i = 0; i < chosenParkings.length; i++) {
				chosenParkings[i] = true;
			}
		}

		parkingChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.parking);

				builder.setMultiChoiceItems(parkings, chosenParkings,
						new DialogInterface.OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								chosenParkings[which] = isChecked;
							}

						});
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<State, Integer> stateDao;
		List<CharSequence> stateList = new ArrayList<CharSequence>();
		try {
			stateDao = getHelper().getStateDao();
			for (State p : stateDao.queryForAll()) {
				stateList.add(p.getName());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		states = stateList.toArray(new CharSequence[stateList.size()]);
		chosenStates = new boolean[stateList.size()];
		for (int i = 0; i < chosenStates.length; i++) {
			chosenStates[i] = true;
		}

		String chosenStatesSet = prefs.getString("chosenStates",
				gson.toJson(chosenStates));

		chosenStates = gson.fromJson(chosenStatesSet, boolean[].class);

		if (chosenStates.length != states.length) {
			chosenStates = new boolean[stateList.size()];
			for (int i = 0; i < chosenStates.length; i++) {
				chosenStates[i] = true;
			}
		}

		stateChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.state);

				builder.setMultiChoiceItems(states, chosenStates,
						new DialogInterface.OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								chosenStates[which] = isChecked;
							}

						});
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		Dao<Type, Integer> typeDao;
		List<CharSequence> typeList = new ArrayList<CharSequence>();
		try {
			typeDao = getHelper().getTypeDao();
			for (Type p : typeDao.queryForAll()) {
				typeList.add(p.getName());
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}
		types = typeList.toArray(new CharSequence[typeList.size()]);
		chosenTypes = new boolean[typeList.size()];
		for (int i = 0; i < chosenTypes.length; i++) {
			chosenTypes[i] = true;
		}

		String chosenTypesSet = prefs.getString("chosenTypes",
				gson.toJson(chosenStates));

		chosenTypes = gson.fromJson(chosenTypesSet, boolean[].class);

		if (chosenTypes.length != types.length) {
			chosenTypes = new boolean[typeList.size()];
			for (int i = 0; i < chosenTypes.length; i++) {
				chosenTypes[i] = true;
			}
		}

		typeChooseButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.type);

				builder.setMultiChoiceItems(types, chosenTypes,
						new DialogInterface.OnMultiChoiceClickListener() {

							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								chosenTypes[which] = isChecked;
							}

						});
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		priceOption.setSelection(prefs.getInt("priceChosenOption", 0));
		rentOption.setSelection(prefs.getInt("rentChosenOption", 0));
		priceEditText.setText(String.valueOf(prefs.getInt("price", 0)));
		rentEditText.setText(String.valueOf(prefs.getInt("rent", 0)));

		searchButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				progressAsyncTask = new ProgressAsyncTask();
				progressAsyncTask.execute();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		Gson gson = new Gson();
		editor.putInt("chosenOffer", chosenOffer);
		editor.putString("chosenHeatings", gson.toJson(chosenHeatings));
		editor.putString("chosenParkings", gson.toJson(chosenParkings));
		editor.putString("chosenStates", gson.toJson(chosenStates));
		editor.putString("chosenTypes", gson.toJson(chosenTypes));
		editor.putString("county", countyTextView.getText().toString());
		editor.putString("city", cityTextView.getText().toString());
		editor.putInt("priceChosenOption",
				(int) priceOption.getSelectedItemId());
		editor.putInt("rentChosenOption", (int) rentOption.getSelectedItemId());
		if (!priceEditText.getText().toString().equals("")) {
			editor.putInt("price",
					Integer.valueOf(priceEditText.getText().toString()));
		} else {
			editor.putInt("price", 0);
		}
		if (!rentEditText.getText().toString().equals("")) {
			editor.putInt("rent",
					Integer.valueOf(rentEditText.getText().toString()));
		} else {
			editor.putInt("rent", 0);
		}
		editor.commit();
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
					+ "/v1/properties.json?county={county}&city={city}&offer={offer}&heating={heating}&parking={parking}&state={state}&type={type}&price={price}&rent={rent}";

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

			String heating = "";
			for (int i = 0; i < chosenHeatings.length; i++) {
				if (chosenHeatings[i] == true) {
					heating += heatings[i] + ",";
				}
			}
			heating = heating.substring(0, heating.length() - 1);

			String parking = "";
			for (int i = 0; i < chosenParkings.length; i++) {
				if (chosenParkings[i] == true) {
					parking += parkings[i] + ",";
				}
			}
			parking = parking.substring(0, parking.length() - 1);

			String state = "";
			for (int i = 0; i < chosenStates.length; i++) {
				if (chosenStates[i] == true) {
					state += states[i] + ",";
				}
			}
			state = state.substring(0, state.length() - 1);

			String type = "";
			for (int i = 0; i < chosenTypes.length; i++) {
				if (chosenTypes[i] == true) {
					type += types[i] + ",";
				}
			}
			type = type.substring(0, type.length() - 1);
			String pricetext = "";
			if (!priceOption.getSelectedItem().equals("N/A")) {
				pricetext = ((String) priceOption.getSelectedItem()) + ": "
						+ Integer.valueOf(priceEditText.getText().toString());
			}
			String renttext = "";
			if (!rentOption.getSelectedItem().equals("N/A")) {
				renttext = ((String) rentOption.getSelectedItem()) + ": "
						+ Integer.valueOf(rentEditText.getText().toString());
			}

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, countyTextView.getText(),
						cityTextView.getText(), offers[chosenOffer], heating,
						parking, state, type, pricetext, renttext);
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
				properties = (new Gson()).fromJson(returnValue,
						Property[].class);

				try {
					Dao<Property, Integer> propertyDao = getHelper()
							.getPropertyDao();

					for (Property p : propertyDao.queryForAll()) {
						propertyDao.delete(p);
					}

					if (properties != null) {
						for (Property p : properties) {
							propertyDao.create(p);
						}
					}
				} catch (SQLException e) {
					sqlErrorMessage(e);
				}
			}
			dismissProgressDialog();
			Intent intent = new Intent(getBaseContext(), ResultActivity.class);
			startActivity(intent);
		}
	}

}
