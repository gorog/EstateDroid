package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.User;

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
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

public class UserActivity extends ParentActivity {

	final Context context = this;
	String userNameString;

	TextView nameTextView;
	TextView usernameTextView;
	TextView professionTextView;
	TextView telephoneTextView;
	TextView emailTextView;
	UserAsyncTask userAsyncTask;
	User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		Intent intent = getIntent();
		userNameString = intent.getStringExtra("userNameString");
		if (userNameString == null) {
			userNameString = username;
		}

		nameTextView = (TextView) findViewById(R.id.nameTextView);
		usernameTextView = (TextView) findViewById(R.id.usernameTextView);
		professionTextView = (TextView) findViewById(R.id.professionTextView);
		telephoneTextView = (TextView) findViewById(R.id.telephoneTextView);
		emailTextView = (TextView) findViewById(R.id.emailTextView);

		userAsyncTask = new UserAsyncTask();
		userAsyncTask.execute();
	}

	protected void fill() {
		List<User> list = new ArrayList<User>();
		try {
			Dao<User, Integer> dao = getHelper().getUserDao();
			list = dao.queryForAll();
		} catch (SQLException e) {

		}
		if (list.size() > 0) {
			user = list.get(0);
			nameTextView.setText(user.getName());
			usernameTextView.setText(user.getUsername());
			professionTextView.setText(user.getProfession());
			telephoneTextView.setText(user.getPhone());
			emailTextView.setText(user.getEmail());
		}
	}

	class UserAsyncTask extends AsyncTask<Void, Integer, Integer> {

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

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());
			String returnValue = "";
			String url = getString(R.string.base_uri)
					+ "/v1/users/{username}.json";

			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.GET, new HttpEntity<Object>(requestHeaders),
						String.class, userNameString);
				returnValue = response.getBody();
				User user = (new Gson()).fromJson(returnValue, User.class);
				try {
					Dao<User, Integer> dao = getHelper().getUserDao();

					for (User p : dao.queryForAll()) {
						dao.delete(p);
					}

					if (user != null) {
						dao.create(user);
					}
				} catch (SQLException e) {
					sqlException = e;
					return 1;
				}

			} catch (HttpClientErrorException e) {
				if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					// TODO kezelni, ha nem sikerült az authentikáció

				}
				// TODO többi hibaüzenetre is kezelni, pl nem megy a
				// szolgáltatás
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result > 0) {
				sqlErrorMessage(sqlException);
			}
			fill();
			dismissProgressDialog();
		}
	}
}
