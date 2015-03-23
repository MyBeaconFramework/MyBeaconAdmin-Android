package com.brokoli.mybeaconadmin.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Logger;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";

    private Dao<Beacon, Integer> myBeaconIntegerDao = null;
    private RuntimeExceptionDao<Beacon, Integer> myBeaconIntegerRuntimeExceptionDao = null;

    private Dao<Gate, Integer> gateIntegerDao = null;
    private RuntimeExceptionDao<Gate, Integer> gateIntegerRuntimeExceptionDao = null;

    private Dao<MyObject, Integer> myObjectIntegerDao = null;
    private RuntimeExceptionDao<MyObject, Integer> myObjectIntegerRuntimeExceptionDao = null;

    private Dao<User, Integer> userIntegerDao = null;
    private RuntimeExceptionDao<User, Integer> userIntegerRuntimeExceptionDao = null;

    private Dao<Logger, Integer> loggerIntegerDao = null;
    private RuntimeExceptionDao<Logger, Integer> loggerIntegerRuntimeExceptionDao = null;

    // Initialize - http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, context.getPackageName(), null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Beacon.class);
            TableUtils.createTable(connectionSource, Gate.class);
            TableUtils.createTable(connectionSource, MyObject.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Logger.class);
        } catch (SQLException e) {
            //CrashlyticsUtils.log(DatabaseHelper.class.getName(), e);
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");

            TableUtils.dropTable(connectionSource, Beacon.class, true);
            TableUtils.dropTable(connectionSource, Gate.class, true);
            TableUtils.dropTable(connectionSource, MyObject.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Logger.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            //CrashlyticsUtils.log(DatabaseHelper.class.getName(), e);
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void clearAll() {
        getMyBeaconIntegerRuntimeExceptionDao().delete(getMyBeaconIntegerRuntimeExceptionDao().queryForAll());
        getGateIntegerRuntimeExceptionDao().delete(getGateIntegerRuntimeExceptionDao().queryForAll());
        getMyObjectIntegerRuntimeExceptionDao().delete(getMyObjectIntegerRuntimeExceptionDao().queryForAll());
        getUserIntegerRuntimeExceptionDao().delete(getUserIntegerRuntimeExceptionDao().queryForAll());
        getLoggerIntegerRuntimeExceptionDao().delete(getLoggerIntegerRuntimeExceptionDao().queryForAll());
    }

    public Dao<Beacon, Integer> getMyBeaconIntegerDao() throws SQLException {
        if (myBeaconIntegerDao == null) {
            myBeaconIntegerDao = getDao(Beacon.class);
        }
        return myBeaconIntegerDao;
    }

    public RuntimeExceptionDao<Beacon, Integer> getMyBeaconIntegerRuntimeExceptionDao() {
        if (myBeaconIntegerRuntimeExceptionDao == null) {
            myBeaconIntegerRuntimeExceptionDao = getRuntimeExceptionDao(Beacon.class);
        }
        return myBeaconIntegerRuntimeExceptionDao;
    }

    public Dao<Gate, Integer> getGateIntegerDao() throws SQLException {
        if (gateIntegerDao == null) {
            gateIntegerDao = getDao(Gate.class);
        }
        return gateIntegerDao;
    }

    public RuntimeExceptionDao<Gate, Integer> getGateIntegerRuntimeExceptionDao() {
        if (gateIntegerRuntimeExceptionDao == null) {
            gateIntegerRuntimeExceptionDao = getRuntimeExceptionDao(Gate.class);
        }
        return gateIntegerRuntimeExceptionDao;
    }

    // MyObject
    public Dao<MyObject, Integer> getMyObjectIntegerDao() throws SQLException {
        if (myObjectIntegerDao == null) {
            myObjectIntegerDao = getDao(MyObject.class);
        }
        return myObjectIntegerDao;
    }

    public RuntimeExceptionDao<MyObject, Integer> getMyObjectIntegerRuntimeExceptionDao() {
        if (myObjectIntegerRuntimeExceptionDao == null) {
            myObjectIntegerRuntimeExceptionDao = getRuntimeExceptionDao(MyObject.class);
        }
        return myObjectIntegerRuntimeExceptionDao;
    }

    //User
    public Dao<User, Integer> getUserIntegerDao() throws SQLException {
        if (userIntegerDao == null) {
            userIntegerDao = getDao(User.class);
        }
        return userIntegerDao;
    }

    public RuntimeExceptionDao<User, Integer> getUserIntegerRuntimeExceptionDao() {
        if (userIntegerRuntimeExceptionDao == null) {
            userIntegerRuntimeExceptionDao = getRuntimeExceptionDao(User.class);
        }
        return userIntegerRuntimeExceptionDao;
    }

    //Logger
    public Dao<Logger, Integer> getLoggerIntegerDao() throws SQLException {
        if (loggerIntegerDao == null) {
            loggerIntegerDao = getDao(Logger.class);
        }
        return loggerIntegerDao;
    }

    public RuntimeExceptionDao<Logger, Integer> getLoggerIntegerRuntimeExceptionDao() {
        if (loggerIntegerRuntimeExceptionDao == null) {
            loggerIntegerRuntimeExceptionDao = getRuntimeExceptionDao(Logger.class);
        }
        return loggerIntegerRuntimeExceptionDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        myBeaconIntegerDao = null;
        myBeaconIntegerRuntimeExceptionDao = null;
        gateIntegerDao = null;
        gateIntegerRuntimeExceptionDao = null;
        myObjectIntegerDao = null;
        myObjectIntegerRuntimeExceptionDao = null;
        userIntegerDao = null;
        userIntegerRuntimeExceptionDao = null;
        loggerIntegerDao= null;
        loggerIntegerRuntimeExceptionDao = null;
    }

    /**
     * Utility methods
     */

    /**
     * MyBeacon related methods
     */
    public boolean createIfNotExists(Beacon beacon) {
        if(TextUtils.isEmpty(beacon.getRemoteId())){
            // If beacon does not have a remoteId, means that it was not synced yet
            // Therefore, to check if it already exists, need to verify if it matches with UUID, major and minor of other not synced beacon
            try {
                Beacon matchedBeacon = getMyBeaconIntegerRuntimeExceptionDao().queryBuilder().where().isNull(Beacon.REMOTE_ID).and().eq(Beacon.UUID_FIELD,
                        beacon.getUuid()).and().eq(Beacon.MAJOR, beacon.getMajor()).and().eq(Beacon.MINOR, beacon.getMinor()).queryForFirst();
                if(matchedBeacon == null){
                    // Does not exist, create it
                    return getMyBeaconIntegerRuntimeExceptionDao().create(beacon) == 1;
                } else {
                    // Already exists
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            List<Beacon> beacons = getMyBeaconIntegerRuntimeExceptionDao().queryForEq(Beacon.REMOTE_ID, beacon.getRemoteId());
            if(beacons.isEmpty()){
                // Does not exist, create it
                return getMyBeaconIntegerRuntimeExceptionDao().create(beacon) == 1;
            } else {
                // Already exists
                return false;
            }
        }
    }

    public void update(Beacon beacon) {
        getMyBeaconIntegerRuntimeExceptionDao().update(beacon);
    }

    public List<Beacon> getAllNotSyncedMyBeacons() {
        try {
            return getMyBeaconIntegerRuntimeExceptionDao().queryBuilder().where().isNull(Beacon.REMOTE_ID).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gate related methods
     */
    public Beacon findBeacon(int myBeaconId) {
        return getMyBeaconIntegerRuntimeExceptionDao().queryForId(myBeaconId);
    }

    public Beacon findBeacon(String remoteId) {
        try {
            return getMyBeaconIntegerRuntimeExceptionDao().queryBuilder().where().eq(Beacon.REMOTE_ID, remoteId).queryForFirst();
        } catch (SQLException ex){
            Log.d(TAG, ex.getLocalizedMessage());
            return null;
        }
    }

    public boolean createIfNotExists(Gate gate) {
        if(TextUtils.isEmpty(gate.getRemoteId())){
            // If gate does not have a remoteId, means that it was not synced yet
            // Therefore, to check if it already exists, need to verify if it matches with id of beacon A and beacon B of other not synced gate
            try {
                Gate matchedGate = getGateIntegerRuntimeExceptionDao().queryBuilder().where().isNull(Gate.REMOTE_ID).and().eq(Gate.BEACON_A_ID_DB,
                        gate.getBeaconA().getId()).and().eq(Gate.BEACON_B_ID_DB, gate.getBeaconB().getId()).queryForFirst();
                if(matchedGate == null){
                    // Does not exist, create it
                    return getGateIntegerRuntimeExceptionDao().create(gate) == 1;
                } else {
                    // Already exists
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            List<Gate> gates = getGateIntegerRuntimeExceptionDao().queryForEq(Gate.REMOTE_ID, gate.getRemoteId());
            if(gates.isEmpty()){
                // Does not exist, create it
                return getGateIntegerRuntimeExceptionDao().create(gate) == 1;
            } else {
                // Already exists
                return false;
            }
        }
    }

    public void update(Gate gate) {
        getGateIntegerRuntimeExceptionDao().update(gate);
    }

    public List<Gate> getAllNotSyncedGates() {
        try {
            return getGateIntegerRuntimeExceptionDao().queryBuilder().where().isNull(Gate.REMOTE_ID).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * User related methods
     */

    public boolean createIfNotExists(User user) {
        if(getUserIntegerRuntimeExceptionDao().queryForEq(User.USERNAME, user.getUsername()).isEmpty()){
            // Does not exist, create it
            return getUserIntegerRuntimeExceptionDao().create(user) == 1;
        } else {
            // Already exists
            return false;
        }
    }

    /**
     * MyObject related methods
     */
    public List<MyObject> queryAllMyObjects() {
        return getMyObjectIntegerRuntimeExceptionDao().queryForAll();
    }

    public void update(MyObject myObject) {
        getMyObjectIntegerRuntimeExceptionDao().update(myObject);
    }

    public boolean createIfNotExists(MyObject myObject) {
        if(TextUtils.isEmpty(myObject.getRemoteId())){
            // If myObject does not have a remoteId, means that it was not synced yet
            // Therefore, to check if it already exists, need to verify if it matches with id of beacon of other not synced myObject
            try {
                MyObject matchedMyObject = getMyObjectIntegerRuntimeExceptionDao().queryBuilder().where().isNull(MyObject.REMOTE_ID).and()
                        .eq(MyObject.BEACON_ID_DB, myObject.getBeaconId()).queryForFirst();
                if(matchedMyObject == null){
                    // Does not exist, create it
                    return getMyObjectIntegerRuntimeExceptionDao().create(myObject) == 1;
                } else {
                    // Already exists
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            List<MyObject> myObjects = getMyObjectIntegerRuntimeExceptionDao().queryForEq(MyObject.REMOTE_ID, myObject.getRemoteId());
            if(myObjects.isEmpty()){
                // Does not exist, create it
                return getMyObjectIntegerRuntimeExceptionDao().create(myObject) == 1;
            } else {
                // Already exists
                return false;
            }
        }
    }

    public List<MyObject> getAllNotSyncedMyObjects() {
        try {
            return getMyObjectIntegerRuntimeExceptionDao().queryBuilder().where().isNull(MyObject.REMOTE_ID).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Logger related methods
     */

    public Logger findLogger(int id) {
        return loggerIntegerRuntimeExceptionDao.queryForId(id);
    }

    public User findUser(String username) {
        try {
            return getUserIntegerRuntimeExceptionDao().queryBuilder().where().eq(User.USERNAME, username).queryForFirst();
        } catch (SQLException ex){
            Log.d(TAG, ex.getLocalizedMessage());
            return null;
        }
    }

    public Gate findGate(String remoteId) {
        try {
            return getGateIntegerRuntimeExceptionDao().queryBuilder().where().eq(Gate.REMOTE_ID, remoteId).queryForFirst();
        } catch (SQLException ex){
            Log.d(TAG, ex.getLocalizedMessage());
            return null;
        }
    }

    public MyObject findMyObject(String remoteId) {
        try {
            return getMyObjectIntegerRuntimeExceptionDao().queryBuilder().where().eq(MyObject.REMOTE_ID, remoteId).queryForFirst();
        } catch (SQLException ex){
            Log.d(TAG, ex.getLocalizedMessage());
            return null;
        }
    }

    public boolean createIfNotExists(Logger logger) {
        if(getLoggerIntegerRuntimeExceptionDao().queryForEq(Logger.REMOTE_ID, logger.getRemoteId()).isEmpty()){
            // Does not exist, create it
            return getLoggerIntegerRuntimeExceptionDao().create(logger) == 1;
        } else {
            // Already exists
            return false;
        }
    }

    /**
     * Stats
     */

    public Logger findLastLocationBeaconFromUser(int userId) {
        QueryBuilder<Logger, Integer> loggerIntegerQueryBuilder = getLoggerIntegerRuntimeExceptionDao().queryBuilder();
        try {
            loggerIntegerQueryBuilder.where().eq(Logger.USER_ID_DB, userId).and().eq(Logger.TYPE, Logger.USER_PASSED_BY_SECTOR);
            loggerIntegerQueryBuilder.orderBy(Logger.DATE, false);
            Logger logger1 = loggerIntegerQueryBuilder.queryForFirst();
            if(logger1 != null){
                return logger1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Logger findLastLocationGateFromUser(int userId) {
        QueryBuilder<Logger, Integer> loggerIntegerQueryBuilder = getLoggerIntegerRuntimeExceptionDao().queryBuilder();
        try {
            loggerIntegerQueryBuilder.where().eq(Logger.USER_ID_DB, userId).and().eq(Logger.TYPE, Logger.USER_PASSED_BY_GATE);
            loggerIntegerQueryBuilder.orderBy(Logger.DATE, false);
            Logger logger1 = loggerIntegerQueryBuilder.queryForFirst();
            if(logger1 != null){
                return logger1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Logger findLastLocationBeaconFromObject(int myObjectId) {
        QueryBuilder<Logger, Integer> loggerIntegerQueryBuilder = getLoggerIntegerRuntimeExceptionDao().queryBuilder();
        try {
            loggerIntegerQueryBuilder.where().eq(Logger.MY_OBJECT_ID_DB, myObjectId).and().eq(Logger.TYPE, Logger.USER_PASSED_BY_OBJECT);
            loggerIntegerQueryBuilder.orderBy(Logger.DATE, false);
            Logger logger1 = loggerIntegerQueryBuilder.queryForFirst();
            if(logger1 != null){
                return logger1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
