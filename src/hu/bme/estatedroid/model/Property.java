package hu.bme.estatedroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "PROPERTY")
public class Property {

	@DatabaseField(id = true)
	private Integer id;

	@DatabaseField
	private String user;

	@DatabaseField
	private String country;

	@DatabaseField
	private String county;

	@DatabaseField
	private String city;

	@DatabaseField
	private String street;

	@DatabaseField
	private String house_number;

	@DatabaseField
	private String floor;

	@DatabaseField
	private String room;

	@DatabaseField
	private Integer longitude;

	@DatabaseField
	private Integer latitude;

	@DatabaseField
	private String offer;

	@DatabaseField
	private String type;

	@DatabaseField
	private Float price;

	@DatabaseField
	private Float rent;

	@DatabaseField
	private Float place;

	@DatabaseField
	private String state;

	@DatabaseField
	private String rooms;

	@DatabaseField
	private String heating;

	@DatabaseField
	private boolean elevator;

	@DatabaseField
	private String parking;

	@DatabaseField
	private String comment;

	@DatabaseField
	private String timestamp;

	public Property() {
		super();
	}

	public Property(Integer id, String user, String country, String county,
			String city, String street, String house_number, String floor,
			String room, Integer longitude, Integer latitude, String offer,
			String type, Float price, Float rent, Float place, String state,
			String rooms, String heating, boolean elevator, String parking,
			String comment, String timestamp) {
		super();
		this.id = id;
		this.user = user;
		this.country = country;
		this.county = county;
		this.city = city;
		this.street = street;
		this.house_number = house_number;
		this.floor = floor;
		this.room = room;
		this.longitude = longitude;
		this.latitude = latitude;
		this.offer = offer;
		this.type = type;
		this.price = price;
		this.rent = rent;
		this.place = place;
		this.state = state;
		this.rooms = rooms;
		this.heating = heating;
		this.elevator = elevator;
		this.parking = parking;
		this.comment = comment;
		this.timestamp = timestamp;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getHouse_number() {
		return house_number;
	}

	public void setHouse_number(String house_number) {
		this.house_number = house_number;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Integer getLongitude() {
		return longitude;
	}

	public void setLongitude(Integer longitude) {
		this.longitude = longitude;
	}

	public Integer getLatitude() {
		return latitude;
	}

	public void setLatitude(Integer latitude) {
		this.latitude = latitude;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getRent() {
		return rent;
	}

	public void setRent(Float rent) {
		this.rent = rent;
	}

	public Float getPlace() {
		return place;
	}

	public void setPlace(Float place) {
		this.place = place;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRooms() {
		return rooms;
	}

	public void setRooms(String rooms) {
		this.rooms = rooms;
	}

	public String getHeating() {
		return heating;
	}

	public void setHeating(String heating) {
		this.heating = heating;
	}

	public boolean isElevator() {
		return elevator;
	}

	public void setElevator(boolean elevator) {
		this.elevator = elevator;
	}

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
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

}
