package hu.bme.estatedroid.model;

public class FavoriteProperty {
	Property property;
	int favoriteId;

	public FavoriteProperty(Property property, int favoriteId) {
		this.property = property;
		this.favoriteId = favoriteId;
	}

	public Property getProperty() {
		return property;
	}

	public int getFavoriteId() {
		return favoriteId;
	}
}