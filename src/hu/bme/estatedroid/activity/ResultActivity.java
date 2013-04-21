package hu.bme.estatedroid.activity;

import hu.bme.estatedroid.ParentActivity;
import hu.bme.estatedroid.R;
import hu.bme.estatedroid.helper.PropertyAdapter;
import hu.bme.estatedroid.model.Property;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.GridView;

import com.j256.ormlite.dao.Dao;

public class ResultActivity extends ParentActivity {
	GridView gridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results);

		gridView = (GridView) findViewById(R.id.properties);
		
		fillGrid();
	}
	
	protected void onResume(){
		super.onResume();
		fillGrid();
		
	}
	private void fillGrid(){
		List<Property> list = new ArrayList<Property>();
		try {
			Dao<Property, Integer> propertyDao = getHelper()
					.getPropertyDao();
			list = propertyDao.queryForAll();
		} catch (SQLException e) {

		}
		
		gridView.setAdapter(new PropertyAdapter(this,list.toArray(new Property[list.size()])));
	}
}
