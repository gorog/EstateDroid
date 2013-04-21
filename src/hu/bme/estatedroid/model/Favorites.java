package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "FAVORITES")
public class Favorites {
	@DatabaseField
	private Integer id;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private User user;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private Property property;

	public Favorites() {
		super();
	}

	public Favorites(Integer id, User user, Property property) {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

}
