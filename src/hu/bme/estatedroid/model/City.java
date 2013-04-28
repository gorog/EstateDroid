package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CITY")
public class City {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String name;

	@DatabaseField
	private String zipCode;

	@DatabaseField
	private int county;

	public City() {
		super();
	}

	public City(Integer id, String name, String zipCode, int county) {
		super();
		this.id = id;
		this.name = name;
		this.zipCode = zipCode;
		this.county = county;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public int getCounty() {
		return county;
	}

	public void setCounty(int county) {
		this.county = county;
	}

}
