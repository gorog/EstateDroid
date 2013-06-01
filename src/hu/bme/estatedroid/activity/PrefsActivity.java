package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PrefsActivity extends PreferenceActivity {

	Button refreshDataButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		setContentView(R.layout.activity_preferences);

		refreshDataButton = (Button) findViewById(R.id.refresh_data);
		refreshDataButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(getBaseContext(),
						DataRefreshActivity.class);
				startActivity(intent);
			}
		});
	}
}