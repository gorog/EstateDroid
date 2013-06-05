package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.City;
import hu.bme.estatedroid.model.Country;
import hu.bme.estatedroid.model.County;
import hu.bme.estatedroid.model.Heating;
import hu.bme.estatedroid.model.NotificationType;
import hu.bme.estatedroid.model.Offer;
import hu.bme.estatedroid.model.Parking;
import hu.bme.estatedroid.model.State;
import hu.bme.estatedroid.model.Type;

import java.sql.SQLException;
import java.util.Collections;

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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class DataRefreshActivity extends ParentActivity {

	public DataRefreshActivity() {
		super("DataRefreshActivity");
	}

	final Context context = this;
	protected static final String TAG = DataRefreshActivity.class
			.getSimpleName();
	private int finishedTasks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datarefresh);
		finishedTasks = 0;
	}

	@Override
	public void onResume() {
		super.onResume();

		TextView citiesRefreshText = (TextView) findViewById(R.id.cities_refresh);
		TextView countriesRefreshText = (TextView) findViewById(R.id.countries_refresh);
		TextView countiesRefreshText = (TextView) findViewById(R.id.counties_refresh);
		TextView heatingsRefreshText = (TextView) findViewById(R.id.heatings_refresh);
		TextView notificationTypesRefreshText = (TextView) findViewById(R.id.notificationtype_refresh);
		TextView offersRefreshText = (TextView) findViewById(R.id.offers_refresh);
		TextView parkingsRefreshText = (TextView) findViewById(R.id.parkings_refresh);
		TextView statesRefreshText = (TextView) findViewById(R.id.states_refresh);
		TextView typesRefreshText = (TextView) findViewById(R.id.types_refresh);

		String citiesURL = getString(R.string.base_uri) + "/v1/cities.json";
		String countriesURL = getString(R.string.base_uri) + "/v1/countries.json";
		String countiesURL = getString(R.string.base_uri) + "/v1/counties.json";
		String heatingsURL = getString(R.string.base_uri) + "/v1/heatings.json";
		String notificationtypesURL = getString(R.string.base_uri)
				+ "/v1/notificationtypes.json";
		String offersURL = getString(R.string.base_uri) + "/v1/offers.json";
		String parkingsURL = getString(R.string.base_uri) + "/v1/parkings.json";
		String statesURL = getString(R.string.base_uri) + "/v1/states.json";
		String typesURL = getString(R.string.base_uri) + "/v1/types.json";

		ProgressAsyncTask citiesTask = new ProgressAsyncTask(citiesRefreshText,
				getText(R.string.cities_refreshing).toString(), getText(
						R.string.cities_refreshed).toString(),
				City.class.toString());
		citiesTask.execute(citiesURL);

		ProgressAsyncTask countriesTask = new ProgressAsyncTask(
				countiesRefreshText, getText(R.string.countries_refreshing)
						.toString(), getText(R.string.countries_refreshed)
						.toString(), Country.class.toString());
		countriesTask.execute(countriesURL);

		ProgressAsyncTask countiesTask = new ProgressAsyncTask(
				countriesRefreshText, getText(R.string.counties_refreshing)
						.toString(), getText(R.string.counties_refreshed)
						.toString(), County.class.toString());
		countiesTask.execute(countiesURL);

		ProgressAsyncTask heatingsTask = new ProgressAsyncTask(
				heatingsRefreshText, getText(R.string.heatings_refreshing)
						.toString(), getText(R.string.heatings_refreshed)
						.toString(), Heating.class.toString());
		heatingsTask.execute(heatingsURL);

		ProgressAsyncTask notificationtypesTask = new ProgressAsyncTask(
				notificationTypesRefreshText, getText(
						R.string.notificationtypes_refreshing).toString(),
				getText(R.string.notificationtypes_refreshed).toString(),
				NotificationType.class.toString());
		notificationtypesTask.execute(notificationtypesURL);

		ProgressAsyncTask offersTask = new ProgressAsyncTask(offersRefreshText,
				getText(R.string.offers_refreshing).toString(), getText(
						R.string.offers_refreshed).toString(),
				Offer.class.toString());
		offersTask.execute(offersURL);

		ProgressAsyncTask parkingsTask = new ProgressAsyncTask(
				parkingsRefreshText, getText(R.string.parkings_refreshing)
						.toString(), getText(R.string.parkings_refreshed)
						.toString(), Parking.class.toString());
		parkingsTask.execute(parkingsURL);

		ProgressAsyncTask statesTask = new ProgressAsyncTask(statesRefreshText,
				getText(R.string.states_refreshing).toString(), getText(
						R.string.states_refreshed).toString(),
				State.class.toString());
		statesTask.execute(statesURL);

		ProgressAsyncTask typesTask = new ProgressAsyncTask(typesRefreshText,
				getText(R.string.types_refreshing).toString(), getText(
						R.string.types_refreshed).toString(),
				Type.class.toString());
		typesTask.execute(typesURL);
	}

	class ProgressAsyncTask extends AsyncTask<String, Integer, Integer> {

		SQLException sqlException;

		TextView textView;
		String pendingMessage;
		String responseMessage;
		String clazz;
		int total;
		int actual;

		ProgressAsyncTask(TextView textView, String pendingMessage,
				String responseMessage, String clazz) {
			this.textView = textView;
			this.pendingMessage = pendingMessage;
			this.responseMessage = responseMessage;
			this.clazz = clazz;
		}

		@Override
		protected void onPreExecute() {
			textView.setText(pendingMessage);
		}

		@Override
		protected Integer doInBackground(String... urls) {

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(10000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "no_data";

			try {
				ResponseEntity<String> response = restTemplate.exchange(
						urls[0], HttpMethod.GET, new HttpEntity<Object>(
								requestHeaders), String.class);
				returnValue = response.getBody();
				if (clazz.equals(City.class.toString())) {
					updateCities(returnValue);
				} else if (clazz.equals(Country.class.toString())) {
					updateCountries(returnValue);
				} else if (clazz.equals(County.class.toString())) {
					updateCounties(returnValue);
				} else if (clazz.equals(Heating.class.toString())) {
					updateHeatings(returnValue);
				} else if (clazz.equals(NotificationType.class.toString())) {
					updateNotificationTypes(returnValue);
				} else if (clazz.equals(Offer.class.toString())) {
					updateOffers(returnValue);
				} else if (clazz.equals(Parking.class.toString())) {
					updateParkings(returnValue);
				} else if (clazz.equals(State.class.toString())) {
					updateStates(returnValue);
				} else if (clazz.equals(Type.class.toString())) {
					updateTypes(returnValue);
				}

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
				textView.setText(responseMessage);
				finishedTasks++;
			}
			if (finishedTasks == 9) {
				finish();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress[0]);
			textView.setText(pendingMessage + " " + actual + "/" + total);
		}

		private void updateCities(String result) {
			City[] objects = (new Gson()).fromJson(result, City[].class);
			try {
				Dao<City, Integer> dao = getHelper().getCityDao();

				for (City p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (City p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateCountries(String result) {
			Country[] objects = (new Gson()).fromJson(result, Country[].class);
			try {
				Dao<Country, Integer> dao = getHelper().getCountryDao();

				for (Country p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (Country p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateCounties(String result) {
			County[] objects = (new Gson()).fromJson(result, County[].class);
			try {
				Dao<County, Integer> dao = getHelper().getCountyDao();

				for (County p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (County p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateHeatings(String result) {
			Heating[] objects = (new Gson()).fromJson(result, Heating[].class);
			try {
				Dao<Heating, Integer> dao = getHelper().getHeatingDao();

				for (Heating p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (Heating p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateNotificationTypes(String result) {
			NotificationType[] objects = (new Gson()).fromJson(result,
					NotificationType[].class);
			try {
				Dao<NotificationType, Integer> dao = getHelper()
						.getNotificationTypeDao();

				for (NotificationType p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (NotificationType p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateOffers(String result) {
			Offer[] objects = (new Gson()).fromJson(result, Offer[].class);
			try {
				Dao<Offer, Integer> dao = getHelper().getOfferDao();

				for (Offer p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (Offer p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateParkings(String result) {
			Parking[] objects = (new Gson()).fromJson(result, Parking[].class);
			try {
				Dao<Parking, Integer> dao = getHelper().getParkingDao();

				for (Parking p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (Parking p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateStates(String result) {
			State[] objects = (new Gson()).fromJson(result, State[].class);
			try {
				Dao<State, Integer> dao = getHelper().getStateDao();

				for (State p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (State p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}

		private void updateTypes(String result) {
			Type[] objects = (new Gson()).fromJson(result, Type[].class);
			try {
				Dao<Type, Integer> dao = getHelper().getTypeDao();

				for (Type p : dao.queryForAll()) {
					dao.delete(p);
				}

				if (objects != null) {
					total = objects.length;
					actual = 0;
					for (Type p : objects) {
						actual++;
						publishProgress(actual);
						dao.create(p);
					}
				}
			} catch (SQLException e) {
				sqlErrorMessage(e);
			}
		}
	}
}
