package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "notification_type")
public class NotificationType {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String name;

	@DatabaseField
	private String description;

	public NotificationType() {
		super();
	}

	public NotificationType(Integer id, String name, String description) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
