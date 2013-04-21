package hu.bme.estatedroid.model;

import java.sql.Timestamp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "NOTIFICATION")
public class Notification {
	
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private User user;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private Property property;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private NotificationType type;

	@DatabaseField
	private String content;

	@DatabaseField
	private Timestamp timestamp;

	public Notification() {
		super();
	}

	public Notification(Integer id, User user, Property property,
			NotificationType type, String content, Timestamp timestamp) {
		super();
		this.id = id;
		this.user = user;
		this.property = property;
		this.type = type;
		this.content = content;
		this.timestamp = timestamp;
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

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
