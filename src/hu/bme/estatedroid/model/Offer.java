package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "OFFER")
public class Offer {
	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String type;

	@DatabaseField
	private String description;

	public Offer() {
		super();
	}

	public Offer(Integer id, String name, String description) {
		super();
		this.id = id;
		this.type = name;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
