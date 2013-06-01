package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "NOTIFICATION")
public class Notification {

	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String user;

	@DatabaseField
	private int property;

	@DatabaseField
	private int type;

	@DatabaseField
	private String content;

	@DatabaseField
	private String timestamp;

	@DatabaseField
	private boolean isread;

	public Notification() {
		super();
	}

	public Notification(Integer id, String user, int property, int type,
			String content, String timestamp, boolean isread) {
		super();
		this.id = id;
		this.user = user;
		this.property = property;
		this.type = type;
		this.content = content;
		this.timestamp = timestamp;
		this.isread = isread;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isIsread() {
		return isread;
	}

	public void setIsread(boolean isread) {
		this.isread = isread;
	}

}
