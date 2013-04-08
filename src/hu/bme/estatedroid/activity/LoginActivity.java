package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;

import java.util.Collections;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity {

	protected static final String TAG = LoginActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	Button loginButton;

	AsyncTask<Void, Void, Object> progressAsyncTask;
	String username;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				EditText editText = (EditText) findViewById(R.id.user_name);
				username = editText.getText().toString();

				editText = (EditText) findViewById(R.id.password);
				password = editText.getText().toString();

				progressAsyncTask = new AsyncTask<Void, Void, Object>() {
					@Override
					protected void onPreExecute() {
						showLoadingProgressDialog();
					}

					@Override
					protected Object doInBackground(Void... arg0) {
						final String url = getString(R.string.base_uri)
								+ "/users";

						HttpAuthentication authHeader = new HttpBasicAuthentication(
								username, password);
						HttpHeaders requestHeaders = new HttpHeaders();

						requestHeaders.setAuthorization(authHeader);
						requestHeaders.setAccept(Collections
								.singletonList(MediaType.APPLICATION_JSON));

						RestTemplate restTemplate = new RestTemplate();
						restTemplate.getMessageConverters().add(
								new StringHttpMessageConverter());

						try {
							// Make the network request
							Log.d(TAG, url);
							ResponseEntity<String> response = restTemplate
									.exchange(url, HttpMethod.GET,
											new HttpEntity<Object>(
													requestHeaders),
											String.class);
							Log.d(TAG, response.getBody());
						} catch (HttpClientErrorException e) {
							Log.e(TAG, e.getLocalizedMessage(), e);
						}

						return null;
					}

					@Override
					protected void onPostExecute(Object result) {
						dismissProgressDialog();
					}

				};
				progressAsyncTask.execute();
				
			}
		});

	}

	public void showLoadingProgressDialog() {
		this.showProgressDialog(getString(R.string.loading_please_wait));
	}

	public void showProgressDialog(CharSequence message) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setIndeterminate(true);
		}

		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
