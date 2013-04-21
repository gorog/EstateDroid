package hu.bme.estatedroid;

import hu.bme.estatedroid.activity.LoginActivity;
import hu.bme.estatedroid.activity.PrefsActivity;
import hu.bme.estatedroid.activity.SearchActivity;
import hu.bme.estatedroid.helper.DatabaseHelper;

import android.app.ProgressDialog;
import android.content.Intent;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class ParentActivity extends SherlockActivity {
	private DatabaseHelper databaseHelper = null;
	protected static final String TAG = LoginActivity.class.getSimpleName();
	private ProgressDialog progressDialog;

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
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
}
