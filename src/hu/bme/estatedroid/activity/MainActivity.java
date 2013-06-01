package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.Notification;

import java.sql.SQLException;
import java.util.Collections;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class MainActivity extends ParentActivity {
	NotificationAsyncTask progressAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		progressAsyncTask = new NotificationAsyncTask();
		progressAsyncTask.execute();
	}

	class NotificationAsyncTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected Void doInBackground(Void... urls) {

			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);
			requestHeaders.setAccept(Collections
					.singletonList(MediaType.APPLICATION_JSON));

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "";
			String url = getString(R.string.base_uri)
					+ "/v1/notifications.json";

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class);
				returnValue = response.getBody();
				Notification[] objects = (new Gson()).fromJson(returnValue,
						Notification[].class);
				try {
					Dao<Notification, Integer> dao = getHelper()
							.getNotificationDao();

					for (Notification p : dao.queryForAll()) {
						dao.delete(p);
					}

					if (objects != null) {
						for (Notification p : objects) {
							dao.create(p);
						}
					}
				} catch (SQLException e) {
					sqlErrorMessage(e);
				}

			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					// TODO kezelni, ha nem sikerült az authentikáció

				}
				// TODO többi hibaüzenetre is kezelni, pl nem megy a
				// szolgáltatás
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			refreshNotification(menu);
			dismissProgressDialog();
		}

	}

}
