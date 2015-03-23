package com.brokoli.mybeaconadmin.data;

import com.j256.ormlite.field.DatabaseField;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;

public class Logger {
    public static final int USER_PASSED_BY_SECTOR = 1;
    public static final int USER_PASSED_BY_GATE = 2;
    public static final int USER_PASSED_BY_OBJECT = 3;

    public static final String REMOTE_ID = "remoteId";
    public static final String TYPE = "type";
    public static final String USERNAME = "username";
    public static final String BEACON_ID = "beaconId";
    public static final String OBJECT_ID = "objectId";
    public static final String GATE_ID = "gateId";
    public static final String LAST_GATE_BEACON_ID = "lastGateBeaconId";
    public static final String LAST_BEACON_ID = "lastBeaconId";

    public static final String USER_ID_DB = "user_id";
    public static final String BEACON_ID_DB = "beacon_id";
    public static final String GATE_ID_DB = "gate_id";
    public static final String MY_OBJECT_ID_DB = "myObject_id";
    public static final String DATE = "date";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String remoteId;

    @DatabaseField
    private Date date;

    @DatabaseField
    private int type;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Beacon beacon;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Gate gate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private MyObject myObject;

    public Logger(){

    }

    public Logger(String remoteId, Date date, User user, Beacon beacon){
        this.remoteId = remoteId;
        this.date = date;
        this.user = user;
        this.beacon = beacon;
        this.type = USER_PASSED_BY_SECTOR;
    }

    public Logger(String remoteId, Date date, User user, Gate gate, Beacon beacon){
        this.remoteId = remoteId;
        this.date = date;
        this.user = user;
        this.gate = gate;
        this.beacon = beacon;
        this.type = USER_PASSED_BY_GATE;
    }

    public Logger(String remoteId, Date date, MyObject myObject, Beacon beacon){
        this.remoteId = remoteId;
        this.date = date;
        this.myObject = myObject;
        this.beacon = beacon;
        this.type = USER_PASSED_BY_OBJECT;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public String getDisplayableType() {
        switch (type){
            case USER_PASSED_BY_SECTOR: return "UserPassedBySector";
            case USER_PASSED_BY_GATE: return "UserPassedByGate";
            case USER_PASSED_BY_OBJECT: return "UserPassedByObject";
            default: return "Unrecognized";
        }
    }

    public String getDisplayableDate() {
        if(date == null){
            return "Date not found";
        }
        PrettyTime p = new PrettyTime(Locale.getDefault());
        return p.format(date);
    }

    public String getInfo1() {
        if(type == USER_PASSED_BY_SECTOR || type == USER_PASSED_BY_GATE){
            return "User: " + user.getUsername();
        } else if(type == USER_PASSED_BY_OBJECT){
            return "Objeto: " + myObject.getDescription();
        } else {
            return "No info";
        }
    }

    public String getInfo2() {
        if(type == USER_PASSED_BY_SECTOR || type == USER_PASSED_BY_OBJECT){
            return "Beacon: " + beacon.getDescription();
        } else if(type == USER_PASSED_BY_GATE){
            return "Gate: " + gate.getDescription();
        } else {
            return "No info";
        }
    }

    public String getInfo3() {
        if(type == USER_PASSED_BY_SECTOR || type == USER_PASSED_BY_OBJECT){
            return "";
        } else if(type == USER_PASSED_BY_GATE){
            return "Saída: " + beacon.getDescription();
        } else {
            return "No info";
        }
    }

    public User getUser() {
        return user;
    }

    public Beacon getBeacon() {
        return beacon;
    }

    public Gate getGate() {
        return gate;
    }

    public MyObject getMyObject() {
        return myObject;
    }

    public int getType() {
        return type;
    }
}
