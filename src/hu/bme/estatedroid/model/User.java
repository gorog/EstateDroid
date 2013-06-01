package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "USER")
public class User {

	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String username;

	@DatabaseField
	private String name;

	@DatabaseField
	private String profession;

	@DatabaseField
	private String phone;

	@DatabaseField
	private String email;

	public User() {
		super();
	}

	public User(Integer id, String username, String name, String profession,
			String phone, String email) {
		super();
		this.id = id;
		this.username = username;
		this.name = name;
		this.profession = profession;
		this.phone = phone;
		this.email = email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
