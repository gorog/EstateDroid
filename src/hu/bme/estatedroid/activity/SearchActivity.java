package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.ParentActivity;
import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.Property;

import java.sql.SQLException;
import java.util.Collections;

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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class SearchActivity extends ParentActivity {
	String username;
	String password;
	AsyncTask<Void, Void, String> progressAsyncTask;
	TextView responseText;
	Property[] properties;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		username = prefs.getString("username", "");
		password = prefs.getString("password", "");
		responseText = (TextView) findViewById(R.id.test);

		progressAsyncTask = new AsyncTask<Void, Void, String>() {
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
				requestHeaders.setAccept(Collections
						.singletonList(MediaType.APPLICATION_JSON));

				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new StringHttpMessageConverter());
				String returnValue = "";

				try {
					// Make the network request
					Log.d(TAG, url);
					ResponseEntity<String> response = restTemplate.exchange(
							url, HttpMethod.GET, new HttpEntity<Object>(
									requestHeaders), String.class);
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
				dismissProgressDialog();
				properties = (new Gson()).fromJson(result, Property[].class);

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
					// TODO kezelni
				}
				responseText.setText(result);
				Intent intent = new Intent(getBaseContext(),
						ResultActivity.class);
				startActivity(intent);
			}

		};
		progressAsyncTask.execute();
	}

}
