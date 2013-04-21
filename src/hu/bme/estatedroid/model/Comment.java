package hu.bme.estatedroid.model;

import java.sql.Timestamp;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "COMMENT")
public class Comment {

	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private User user;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private Property property;

	@DatabaseField
	private String comment;

	@DatabaseField
	private Timestamp timestamp;

	@DatabaseField(foreign=true, foreignAutoCreate=true, foreignAutoRefresh=true)
	private Comment commentId;

	public Comment() {
		super();
	}

	public Comment(Integer id, User user, Property property, String comment,
			Timestamp timestamp, Comment commentId) {
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Comment getCommentId() {
		return commentId;
	}

	public void setCommentId(Comment commentId) {
		this.commentId = commentId;
	}

}
