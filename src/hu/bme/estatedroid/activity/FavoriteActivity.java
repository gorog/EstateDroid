package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.FavoriteProperty;
import hu.bme.estatedroid.model.Favorites;
import hu.bme.estatedroid.model.Property;
import hu.bme.estatredroid.adapter.FavoritePropertyAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class FavoriteActivity extends ParentListActivity {

	final Context context = this;
	Property[] properties;
	FavoritePropertyAdapter favoritePropertyAdapter;
	FavoriteAsyncTask progressAsyncTask;
	ArrayAdapter<String> options;
	ListView listView;
	Property dummyProperty;

	public FavoriteActivity() {
		super("FavoriteActivity");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = getListView();
		dummyProperty = new Property(0, "null", "null", "null",
				getString(R.string.no_favorite), "null", "null", "null",
				"null", 0, 0, "", "null", 0.0f, 0.0f, 0.0f, "null", "null",
				"null", false, "null", "", "");
	}

	@Override
	protected void onResume() {
		super.onResume();

		progressAsyncTask = new FavoriteAsyncTask();
		progressAsyncTask.execute();
	}

	protected void fill() {
		List<Favorites> list = new ArrayList<Favorites>();

		try {
			Dao<Favorites, Integer> favoritesDao = getHelper()
					.getFavoritesDao();
			list = favoritesDao.queryForAll();
		} catch (SQLException e) {

		}

		try {
			Dao<Property, Integer> propertyDao = getHelper().getPropertyDao();

			for (Property p : propertyDao.queryForAll()) {
				propertyDao.delete(p);
			}
		} catch (SQLException e) {
			sqlErrorMessage(e);
		}

		favoritePropertyAdapter = new FavoritePropertyAdapter(this,
				new ArrayList<FavoriteProperty>(), username, password);
		setListAdapter(favoritePropertyAdapter);

		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				options = new ArrayAdapter<String>(context,
						android.R.layout.select_dialog_item);

				options.add(context.getString(R.string.jump_to_property));
				options.add(context.getString(R.string.delete));
				options.add(context.getString(R.string.cancel));

				builder.setAdapter(
						options,
						new FavoriteOnClickListener(parent
								.getItemIdAtPosition(position)));
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		addFavoriteProperty(dummyProperty, -1);

		for (Favorites f : list) {
			PropertyAsyncTask task = new PropertyAsyncTask(f.getProperty(),
					f.getId());
			task.execute();
		}
	}

	public class FavoriteOnClickListener implements
			DialogInterface.OnClickListener {

		int position;
		long id;

		public FavoriteOnClickListener(long id) {
			this.id = id;
		}

		public void onClick(DialogInterface dialog, int which) {
			if (options.getItem(which).equals(
					context.getString(R.string.delete))) {
				FavoriteDeleteAsyncTask task = new FavoriteDeleteAsyncTask(
						(int) id);
				task.execute();
			} else if (options.getItem(which).equals(
					context.getString(R.string.jump_to_property))) {
				Intent intent = new Intent(context, DetailsActivity.class);
				intent.putExtra("propertyId", favoritePropertyAdapter
						.getItemById((int) id).getProperty().getId());
				startActivity(intent);
			} else if (options.getItem(which).equals(
					context.getString(R.string.cancel))) {
				dialog.dismiss();
			}
		}

	};

	protected void addFavoriteProperty(Property property, int favoriteId) {
		if (favoritePropertyAdapter.getItemById(-1) != null) {
			favoritePropertyAdapter = new FavoritePropertyAdapter(this,
					new ArrayList<FavoriteProperty>(), username, password);
			setListAdapter(favoritePropertyAdapter);
		}
		favoritePropertyAdapter.add(new FavoriteProperty(property, favoriteId));
		favoritePropertyAdapter.notifyDataSetChanged();
	}

	protected void removeFavoriteProperty(FavoriteProperty property) {
		favoritePropertyAdapter.remove(property);
		favoritePropertyAdapter.notifyDataSetChanged();
	}

	class FavoriteAsyncTask extends AsyncTask<Void, Integer, Integer> {

		SQLException sqlException;

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Integer doInBackground(Void... urls) {

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);

			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "no_data";
			String url = getString(R.string.base_uri) + "/v1/favorites.json";

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class);
				returnValue = response.getBody();
				Favorites[] objects = (new Gson()).fromJson(returnValue,
						Favorites[].class);
				try {
					Dao<Favorites, Integer> dao = getHelper().getFavoritesDao();

					for (Favorites p : dao.queryForAll()) {
						dao.delete(p);
					}

					if (objects != null) {
						for (Favorites p : objects) {
							dao.create(p);
						}
					}
				} catch (SQLException e) {
					sqlException = e;
					return 1;
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
				fill();
			}
			dismissProgressDialog();
		}
	}

	class FavoriteDeleteAsyncTask extends AsyncTask<Void, Integer, Integer> {

		int id;
		SQLException sqlException;

		FavoriteDeleteAsyncTask(int id) {
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
					+ "/v1/favorites/{id}.json";

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
			} else {
				removeFavoriteProperty(favoritePropertyAdapter.getItemById(id));
			}
			dismissProgressDialog();
		}
	}

	class PropertyAsyncTask extends AsyncTask<Void, Integer, Integer> {
		int propertyId;
		int favoriteId;
		Property property;
		SQLException sqlException;

		PropertyAsyncTask(int propertyId, int favoriteId) {
			this.propertyId = propertyId;
			this.favoriteId = favoriteId;
		}

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Integer doInBackground(Void... urls) {

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setConnectTimeout(1000);
			RestTemplate restTemplate = new RestTemplate(requestFactory);
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "no_data";
			String url = getString(R.string.base_uri)
					+ "/v1/properties/{id}.json";
			property = null;
			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, propertyId);
				returnValue = response.getBody();
				property = (new Gson()).fromJson(returnValue, Property.class);
				try {
					Dao<Property, Integer> dao = getHelper().getPropertyDao();

					if (property != null) {
						dao.create(property);
					}
				} catch (SQLException e) {
					sqlErrorMessage(e);
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
				if (property != null) {
					addFavoriteProperty(property, favoriteId);
				}
			}
			dismissProgressDialog();
		}
	}

}
