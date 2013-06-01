package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.DatabaseHelper;
import hu.bme.estatedroid.model.Notification;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class ParentListActivity extends SherlockListActivity {
	protected String username;
	protected String password;
	private DatabaseHelper databaseHelper = null;
	protected static final String TAG = ParentListActivity.class
			.getSimpleName();
	private ProgressDialog progressDialog;
	protected HttpAuthentication authHeader;
	protected Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		username = prefs.getString("username", "");
		password = prefs.getString("password", "");
		authHeader = new HttpBasicAuthentication(username, password);
	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
	}

	protected DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public void showLoadingProgressDialog() {
		showProgressDialog(getString(R.string.loading_please_wait));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		this.menu = menu;

		refreshNotification(menu);

		return true;
	}

	public void refreshNotification(Menu menu) {
		List<Notification> list = new ArrayList<Notification>();
		try {
			Dao<Notification, Integer> notificationDao = getHelper()
					.getNotificationDao();
			QueryBuilder<Notification, Integer> queryBuilder = notificationDao
					.queryBuilder();
			queryBuilder.where().eq("isread", false);
			PreparedQuery<Notification> preparedQuery = queryBuilder.prepare();

			list = notificationDao.query(preparedQuery);
		} catch (SQLException e) {

		}

		if (list.size() > 0) {
			menu.findItem(R.id.action_notifications).setIcon(
					R.drawable.social_chat_red);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent intent = new Intent(getBaseContext(), PrefsActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_search) {
			Intent intent = new Intent(getBaseContext(), SearchActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_notifications) {
			Intent intent = new Intent(getBaseContext(),
					NotificationActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_upload) {
			Intent intent = new Intent(getBaseContext(), UploadActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_favorites) {
			Intent intent = new Intent(getBaseContext(), FavoriteActivity.class);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_own) {
			Intent intent = new Intent(getBaseContext(), UserActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void sqlErrorMessage(SQLException e) {
		Toast.makeText(getApplicationContext(), e.getMessage(),
				Toast.LENGTH_SHORT).show();
	}
}
