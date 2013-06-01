package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.NotificationAdapter;
import hu.bme.estatedroid.model.Notification;
import hu.bme.estatedroid.model.NotificationType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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
import com.j256.ormlite.stmt.UpdateBuilder;

public class NotificationActivity extends ParentListActivity {

	final Context context = this;
	Notification[] notifications;
	NotificationAdapter notificationAdapter;
	Map<Integer, String> notificationTypes;
	NotificationAsyncTask progressAsyncTask;
	ArrayAdapter<String> options;
	ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listView = getListView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		progressAsyncTask = new NotificationAsyncTask();
		progressAsyncTask.execute();
	}

	protected void fill() {
		List<Notification> list = new ArrayList<Notification>();
		try {
			Dao<Notification, Integer> notificationDao = getHelper()
					.getNotificationDao();
			list = notificationDao.queryForAll();
		} catch (SQLException e) {

		}

		notifications = list.toArray(new Notification[list.size()]);

		notificationTypes = new HashMap<Integer, String>();
		List<NotificationType> typeList = new ArrayList<NotificationType>();
		try {
			Dao<NotificationType, Integer> notificationTypeDao = getHelper()
					.getNotificationTypeDao();
			typeList = notificationTypeDao.queryForAll();
			for (NotificationType n : typeList) {
				notificationTypes.put(n.getId(), n.getName());
			}
		} catch (SQLException e) {

		}

		notificationAdapter = new NotificationAdapter(this, notifications,
				notificationTypes);
		setListAdapter(notificationAdapter);

		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				options = new ArrayAdapter<String>(context,
						android.R.layout.select_dialog_item);
				options.add(context.getString(R.string.mark_as_read));
				options.add(context.getString(R.string.mark_as_unread));
				options.add(context.getString(R.string.jump_to_property));
				options.add(context.getString(R.string.jump_to_user));
				options.add(context.getString(R.string.cancel));

				builder.setAdapter(options, new NotificationOnClickListener(
						parent.getItemIdAtPosition(position)));
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		refreshNotification(menu);
	}

	public class NotificationOnClickListener implements
			DialogInterface.OnClickListener {

		int position;
		long id;

		public NotificationOnClickListener(long id) {
			this.id = id;

		}

		public void onClick(DialogInterface dialog, int which) {
			if (options.getItem(which).equals(
					context.getString(R.string.mark_as_read))) {
				updateNotification(id, true);
				fill();
			} else if (options.getItem(which).equals(
					context.getString(R.string.mark_as_unread))) {
				updateNotification(id, false);
			} else if (options.getItem(which).equals(
					context.getString(R.string.jump_to_property))) {
				Intent intent = new Intent(context, DetailsActivity.class);
				intent.putExtra("propertyId",
						notificationAdapter.getItemById((int) id).getProperty());
				startActivity(intent);
			} else if (options.getItem(which).equals(
					context.getString(R.string.jump_to_user))) {
				// TODO
			} else if (options.getItem(which).equals(
					context.getString(R.string.cancel))) {
				dialog.dismiss();
			}
		}

	};

	protected void updateNotification(long id, boolean isread) {
		NotificationUpdateAsyncTask task = new NotificationUpdateAsyncTask(id,
				isread);
		task.execute();
	}

	protected void changeIsunread(long id) {
		notificationAdapter.setItemById(id);
		setListAdapter(notificationAdapter);

		try {
			Dao<Notification, Integer> notificationDao = getHelper()
					.getNotificationDao();
			UpdateBuilder<Notification, Integer> updateBuilder = notificationDao
					.updateBuilder();
			updateBuilder.updateColumnValue("isread", notificationAdapter
					.getItemById(id).isIsread());
			updateBuilder.where().eq("id", id);
			updateBuilder.update();
		} catch (SQLException e) {

		}

		refreshNotification(menu);
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
			fill();
			dismissProgressDialog();
		}

	}

	class NotificationUpdateAsyncTask extends AsyncTask<Void, Integer, String> {

		long id;
		boolean isread;

		public NotificationUpdateAsyncTask(long id, boolean isread) {
			this.id = id;
			this.isread = isread;
		}

		@Override
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected String doInBackground(Void... urls) {
			String url = getString(R.string.base_uri)
					+ "/v1/notifications/{id}.json";

			HttpAuthentication authHeader = new HttpBasicAuthentication(
					username, password);
			HttpHeaders requestHeaders = new HttpHeaders();

			requestHeaders.setAuthorization(authHeader);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(
					new FormHttpMessageConverter());
			restTemplate.getMessageConverters().add(
					new StringHttpMessageConverter());

			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("isread", String.valueOf(isread));

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
					map, requestHeaders);
			String returnValue = "";
			try {
				ResponseEntity<String> response = restTemplate.exchange(url,
						HttpMethod.POST, request, String.class, id);
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
			if (result.equals("\"ok\"")) {
				changeIsunread(id);
			}
			dismissProgressDialog();
		}

	}
}
