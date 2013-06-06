package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.adapter.PropertyAdapter;
import hu.bme.estatedroid.model.Property;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.j256.ormlite.dao.Dao;

//TODO ha nincs egy elem se, akkor csináljon vmit
public class ResultActivity extends ParentActivity {

	final Context context = this;
	GridView gridView;
	ArrayAdapter<String> options;
	PropertyAdapter propertyAdapter;

	public ResultActivity() {
		super("ResultActivity");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		gridView = (GridView) findViewById(R.id.properties);

		fillGrid();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillGrid();

	}

	private void fillGrid() {
		List<Property> list = new ArrayList<Property>();
		try {
			Dao<Property, Integer> propertyDao = getHelper().getPropertyDao();
			list = propertyDao.queryForAll();
		} catch (SQLException e) {

		}

		propertyAdapter = new PropertyAdapter(this, list, username, password);
		gridView.setAdapter(propertyAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				options = new ArrayAdapter<String>(context,
						android.R.layout.select_dialog_item);

				options.add(context.getString(R.string.jump_to_property));
				options.add(context.getString(R.string.jump_to_user));
				options.add(context.getString(R.string.cancel));

				builder.setAdapter(
						options,
						new PropertyOnClickListener(parent
								.getItemIdAtPosition(position)));
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	public class PropertyOnClickListener implements
			DialogInterface.OnClickListener {

		long id;

		public PropertyOnClickListener(long id) {
			this.id = id;
		}

		public void onClick(DialogInterface dialog, int which) {
			if (options.getItem(which).equals(
					context.getString(R.string.jump_to_property))) {
				Property property = propertyAdapter.getItemById((int) id);
				Intent intent = new Intent(context, DetailsActivity.class);
				intent.putExtra("propertyId", property.getId());
				startActivity(intent);
			} else if (options.getItem(which).equals(
					context.getString(R.string.jump_to_user))) {
				Property property = propertyAdapter.getItemById((int) id);
				Intent intent = new Intent(context, UserActivity.class);
				intent.putExtra("userNameString", property.getUser());
				startActivity(intent);
			} else if (options.getItem(which).equals(
					context.getString(R.string.cancel))) {
				dialog.dismiss();
			}
		}
	};
}
