package com.brokoli.mybeaconadmin.data;

import com.baasbox.android.json.JsonObject;
import com.j256.ormlite.field.DatabaseField;

public class Beacon {
    public static final int SECTOR_BEACON_TYPE = 0;
    public static final int OBJECT_BEACON_TYPE = 1;
    public static final int GATE_BEACON_TYPE = 2;
    public static final int GATE_SECTOR_BEACON_TYPE = 3;

    public static final double NO_LATITUDE = -200;
    public static final double NO_LONGITUDE = -200;

    public static final String REMOTE_ID = "remoteId";
    public static final String BEACON_TYPE = "beaconType";
    public static final String UUID_FIELD = "uuid";
    public static final String MAJOR = "major";
    public static final String MINOR = "minor";
    public static final String DESCRIPTION = "description";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String remoteId;

    @DatabaseField
    private int beaconType;

    @DatabaseField
    private String uuid;

    @DatabaseField
    private int major;

    @DatabaseField
    private int minor;

    @DatabaseField
    private String description;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    public Beacon(){

    }

    public Beacon(String remoteId, int beaconType, String uuid, int major, int minor, String description, double latitude, double longitude) {
        this.remoteId = remoteId;
        this.beaconType = beaconType;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Beacon(int beaconType, String uuid, int major, int minor, String description, double latitude, double longitude) {
        this(null, beaconType, uuid, major, minor, description, latitude, longitude);
    }

    public int getId() {
        return id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public String getDisplayableMajor() {
        return String.valueOf(major);
    }

    public String getDisplayableMinor() {
        return String.valueOf(minor);
    }

    @Override
    public String toString() {
        return "Beacon{" +
                "remoteId=" + remoteId +
                ", beaconType=" + beaconType +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                '}';
    }

    /*
     * Utility methods
     */

    public JsonObject createJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Beacon.UUID_FIELD, uuid);
        jsonObject.put(Beacon.MAJOR, major);
        jsonObject.put(Beacon.MINOR, minor);
        jsonObject.put(Beacon.DESCRIPTION, description);
        jsonObject.put(Beacon.LATITUDE, latitude);
        jsonObject.put(Beacon.LONGITUDE, longitude);
        jsonObject.put(Beacon.BEACON_TYPE, beaconType);
        return jsonObject;
    }


    public String getMyBeaconRowText() {
        return description + " | " + uuid + " | " + major + " | " + minor;
    }

    public CharSequence getAddGateConvertToString(String beaconAutoCompleteTextView) {
        if (beaconAutoCompleteTextView.isEmpty()) {
            return description;
        } else {
            if (description.toLowerCase().contains(beaconAutoCompleteTextView)) {
                return description;
            } else if(uuid.toLowerCase().contains(beaconAutoCompleteTextView)){
                return uuid;
            } else if(getDisplayableMajor().contains(beaconAutoCompleteTextView)){
                return getDisplayableMajor();
            } else if(getDisplayableMinor().contains(beaconAutoCompleteTextView)){
                return getDisplayableMinor();
            } else {
                return description;
            }
        }
    }

    public boolean isSectorBeacon() {
        return beaconType == SECTOR_BEACON_TYPE || beaconType == GATE_SECTOR_BEACON_TYPE;
    }

    public boolean isGateBacon() {
        return beaconType == GATE_BEACON_TYPE || beaconType == GATE_SECTOR_BEACON_TYPE;
    }

    public boolean isObjectBeacon() {
        return beaconType == OBJECT_BEACON_TYPE;
    }
}
