package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "COUNTY")
public class County {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String name;

	@DatabaseField
	private int country;

	public County() {
		super();
	}

	public County(Integer id, String name, int country) {
		super();
		this.id = id;
		this.name = name;
		this.country = country;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}

}
