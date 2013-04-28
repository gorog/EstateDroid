package hu.bme.estatedroid.helper;

import hu.bme.estatedroid.R;
import hu.bme.estatedroid.model.City;
import hu.bme.estatedroid.model.Comment;
import hu.bme.estatedroid.model.Country;
import hu.bme.estatedroid.model.County;
import hu.bme.estatedroid.model.Favorites;
import hu.bme.estatedroid.model.Heating;
import hu.bme.estatedroid.model.Notification;
import hu.bme.estatedroid.model.NotificationType;
import hu.bme.estatedroid.model.Offer;
import hu.bme.estatedroid.model.Parking;
import hu.bme.estatedroid.model.Property;
import hu.bme.estatedroid.model.State;
import hu.bme.estatedroid.model.Type;
import hu.bme.estatedroid.model.User;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "estateDroid.db";
	private static final int DATABASE_VERSION = 3;

	// the DAO object we use to access the SimpleData table
	private Dao<Property, Integer> propertyDao = null;
	private Dao<City, Integer> cityDao = null;
	private Dao<Comment, Integer> commentDao = null;
	private Dao<Country, Integer> countryDao = null;
	private Dao<County, Integer> countyDao = null;
	private Dao<Favorites, Integer> favoritesDao = null;
	private Dao<Heating, Integer> heatingDao = null;
	private Dao<Notification, Integer> notificationDao = null;
	private Dao<NotificationType, Integer> notificationTypeDao = null;
	private Dao<Offer, Integer> offerDao = null;
	private Dao<Parking, Integer> parkingDao = null;
	private Dao<State, Integer> stateDao = null;
	private Dao<Type, Integer> typeDao = null;
	private Dao<User, Integer> userDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION,
				R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Property.class);
			TableUtils.createTable(connectionSource, City.class);
			TableUtils.createTable(connectionSource, Comment.class);
			TableUtils.createTable(connectionSource, Country.class);
			TableUtils.createTable(connectionSource, County.class);
			TableUtils.createTable(connectionSource, Favorites.class);
			TableUtils.createTable(connectionSource, Heating.class);
			TableUtils.createTable(connectionSource, Notification.class);
			TableUtils.createTable(connectionSource, NotificationType.class);
			TableUtils.createTable(connectionSource, Offer.class);
			TableUtils.createTable(connectionSource, Parking.class);
			TableUtils.createTable(connectionSource, State.class);
			TableUtils.createTable(connectionSource, Type.class);
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Property.class, true);
			TableUtils.dropTable(connectionSource, City.class, true);
			TableUtils.dropTable(connectionSource, Comment.class, true);
			TableUtils.dropTable(connectionSource, Country.class, true);
			TableUtils.dropTable(connectionSource, County.class, true);
			TableUtils.dropTable(connectionSource, Favorites.class, true);
			TableUtils.dropTable(connectionSource, Heating.class, true);
			TableUtils.dropTable(connectionSource, Notification.class, true);
			TableUtils
					.dropTable(connectionSource, NotificationType.class, true);
			TableUtils.dropTable(connectionSource, Offer.class, true);
			TableUtils.dropTable(connectionSource, Parking.class, true);
			TableUtils.dropTable(connectionSource, State.class, true);
			TableUtils.dropTable(connectionSource, Type.class, true);
			TableUtils.dropTable(connectionSource, User.class, true);

			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	
	public Dao<Property, Integer> getPropertyDao() throws SQLException {
		if (propertyDao == null) {
			propertyDao = getDao(Property.class);
		}
		return propertyDao;
	}
	public Dao<City, Integer> getCityDao() throws SQLException {
		if (cityDao == null) {
			cityDao = getDao(City.class);
		}
		return cityDao;
	}
	public Dao<Comment, Integer> getCommentDao() throws SQLException {
		if (commentDao == null) {
			commentDao = getDao(Comment.class);
		}
		return commentDao;
	}
	public Dao<Country, Integer> getCountryDao() throws SQLException {
		if (countryDao == null) {
			countryDao = getDao(Country.class);
		}
		return countryDao;
	}
	public Dao<County, Integer> getCountyDao() throws SQLException {
		if (countyDao == null) {
			countyDao = getDao(County.class);
		}
		return countyDao;
	}
	public Dao<Favorites, Integer> getFavoritesDao() throws SQLException {
		if (favoritesDao == null) {
			favoritesDao = getDao(Favorites.class);
		}
		return favoritesDao;
	}
	public Dao<Heating, Integer> getHeatingDao() throws SQLException {
		if (heatingDao == null) {
			heatingDao = getDao(Heating.class);
		}
		return heatingDao;
	}
	public Dao<Notification, Integer> getNotificationDao() throws SQLException {
		if (notificationDao == null) {
			notificationDao = getDao(Notification.class);
		}
		return notificationDao;
	}
	public Dao<NotificationType, Integer> getNotificationTypeDao() throws SQLException {
		if (notificationTypeDao == null) {
			notificationTypeDao = getDao(NotificationType.class);
		}
		return notificationTypeDao;
	}
	public Dao<Offer, Integer> getOfferDao() throws SQLException {
		if (offerDao == null) {
			offerDao = getDao(Offer.class);
		}
		return offerDao;
	}
	public Dao<Parking, Integer> getParkingDao() throws SQLException {
		if (parkingDao == null) {
			parkingDao = getDao(Parking.class);
		}
		return parkingDao;
	}
	public Dao<State, Integer> getStateDao() throws SQLException {
		if (stateDao == null) {
			stateDao = getDao(State.class);
		}
		return stateDao;
	}
	public Dao<Type, Integer> getTypeDao() throws SQLException {
		if (typeDao == null) {
			typeDao = getDao(Type.class);
		}
		return typeDao;
	}
	public Dao<User, Integer> getUserDao() throws SQLException {
		if (userDao == null) {
			userDao = getDao(User.class);
		}
		return userDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		propertyDao = null;
		cityDao = null;
		commentDao = null;
		countryDao = null;
		countyDao = null;
		favoritesDao = null;
		heatingDao = null;
		notificationDao = null;
		notificationTypeDao = null;
		offerDao = null;
		parkingDao = null;
		stateDao = null;
		typeDao = null;
		userDao = null;
	}
}