package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.DatabaseHelper;
import hu.bme.estatedroid.model.City;
import hu.bme.estatedroid.model.Comment;
import hu.bme.estatedroid.model.Country;
import hu.bme.estatedroid.model.County;
import hu.bme.estatedroid.model.Favorites;
import hu.bme.estatedroid.model.Heating;
import hu.bme.estatedroid.model.Notification;
import hu.bme.estatedroid.model.NotificationType;
import hu.bme.estatedroid.model.Offer;
import hu.bme.estatedroid.model.Parking;
import hu.bme.estatedroid.model.Property;
import hu.bme.estatedroid.model.State;
import hu.bme.estatedroid.model.Type;
import hu.bme.estatedroid.model.User;

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
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

public class ParentActivity extends SherlockActivity {
	protected String username;
	protected String password;
	private DatabaseHelper databaseHelper = null;
	protected static final String TAG = ParentActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	protected HttpAuthentication authHeader;
	protected Menu menu;
	private final String className;

	protected ParentActivity(String className) {
		this.className = className;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		username = prefs.getString("username", "");
		password = prefs.getString("password", "");
		authHeader = new HttpBasicAuthentication(username, password);

		if (username.equals("")) {
			Toast.makeText(getApplicationContext(), R.string.bad_username,
					Toast.LENGTH_LONG).show();
			Intent intent = new Intent(getBaseContext(), PrefsActivity.class);
			startActivity(intent);
		}
		if (!className.equals("DataRefreshActivity") && !checkDatabase()) {
			Intent intent = new Intent(getBaseContext(),
					DataRefreshActivity.class);
			startActivity(intent);
		}
	}

	protected void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		username = prefs.getString("username", "");
		password = prefs.getString("password", "");
		authHeader = new HttpBasicAuthentication(username, password);
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
			progressDialog = new ProgressDialog(this, R.style.dialogStyle);
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

	public boolean checkDatabase() {
		boolean returnValue = true;
		try {
			Dao<Property, Integer> propertyDao = getHelper().getPropertyDao();
			if (!propertyDao.isTableExists()) {
				returnValue = false;
				Log.d("hely", "pr");
			}

			Dao<Comment, Integer> commentDao = getHelper().getCommentDao();
			if (!commentDao.isTableExists()) {
				returnValue = false;
				Log.d("hely", "co");
			}

			Dao<Favorites, Integer> favoritesDao = getHelper()
					.getFavoritesDao();
			if (!favoritesDao.isTableExists()) {
				returnValue = false;
				Log.d("hely", "fa");
			}

			Dao<User, Integer> userDao = getHelper().getUserDao();
			if (!userDao.isTableExists()) {
				returnValue = false;
				Log.d("hely", "u");
			}

			Dao<Notification, Integer> notificationDao = getHelper()
					.getNotificationDao();
			if (!notificationDao.isTableExists()) {
				returnValue = false;
				Log.d("hely", "n");
			}

			Dao<City, Integer> cityDao = getHelper().getCityDao();
			if (cityDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "c");
			}

			Dao<Country, Integer> countryDao = getHelper().getCountryDao();
			if (countryDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "cr");
			}

			Dao<County, Integer> countyDao = getHelper().getCountyDao();
			if (countyDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "ct");
			}

			Dao<Heating, Integer> heatingDao = getHelper().getHeatingDao();
			if (heatingDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "h");
			}

			Dao<NotificationType, Integer> notificationTypeDao = getHelper()
					.getNotificationTypeDao();
			if (notificationTypeDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "nt");
			}

			Dao<Offer, Integer> offerDao = getHelper().getOfferDao();
			if (offerDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "o");
			}

			Dao<Parking, Integer> parkingDao = getHelper().getParkingDao();
			if (parkingDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "pa");
			}

			Dao<State, Integer> stateDao = getHelper().getStateDao();
			if (stateDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "s");
			}

			Dao<Type, Integer> typeDao = getHelper().getTypeDao();
			if (typeDao.countOf() == 0) {
				returnValue = false;
				Log.d("hely", "t");
			}

		} catch (SQLException e) {
			returnValue = false;
		}
		return returnValue;
	}

}
