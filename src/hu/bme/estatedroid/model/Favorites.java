package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FAVORITES")
public class Favorites {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String user;

	@DatabaseField
	private int property;

	public Favorites() {
		super();
	}

	public Favorites(Integer id, String user, int property) {
		super();
		this.id = id;
		this.user = user;
		this.property = property;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public int getProperty() {
		return property;
	}

	public void setProperty(int property) {
		this.property = property;
	}

}
