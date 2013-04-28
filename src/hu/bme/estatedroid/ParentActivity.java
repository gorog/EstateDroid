package hu.bme.estatedroid;

import hu.bme.estatedroid.activity.DataRefreshActivity;
import hu.bme.estatedroid.activity.PrefsActivity;
import hu.bme.estatedroid.activity.SearchActivity;
import hu.bme.estatedroid.helper.DatabaseHelper;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ParentActivity extends SherlockActivity {
	protected String username;
	protected String password;
	private DatabaseHelper databaseHelper = null;
	protected static final String TAG = ParentActivity.class.getSimpleName();
	private ProgressDialog progressDialog;
	protected HttpAuthentication authHeader;

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

		return true;
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
					DataRefreshActivity.class);
			startActivity(intent);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
