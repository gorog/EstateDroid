package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "COUNTRY")
public class Country {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String short_name;

	@DatabaseField
	private String name;


	public Country() {
		super();
	}

	public Country(Integer id, String short_name, String name) {
		super();
		this.id = id;
		this.short_name = short_name;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShort_name() {
		return short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
