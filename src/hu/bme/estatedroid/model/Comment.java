package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "COMMENT")
public class Comment {

	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String user;

	@DatabaseField
	private int property;

	@DatabaseField
	private String comment;

	@DatabaseField
	private String timestamp;

	@DatabaseField
	private int commentId;

	public Comment() {
		super();
	}

	public Comment(Integer id, String user, int property, String comment,
			String timestamp, int commentId) {
		super();
		this.id = id;
		this.user = user;
		this.property = property;
		this.comment = comment;
		this.timestamp = timestamp;
		this.commentId = commentId;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

}
