package com.brokoli.mybeaconadmin.data;

import com.baasbox.android.json.JsonObject;
import com.j256.ormlite.field.DatabaseField;

public class MyObject {
    public static final String REMOTE_ID = "remoteId";
    public static final String DESCRIPTION = "description";
    public static final String BEACON_ID = "beaconId";

    public static final String BEACON_ID_DB = "beacon_id";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String remoteId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Beacon beacon;

    @DatabaseField
    private String description;

    public MyObject(){

    }

    public MyObject(String remoteId, Beacon beacon, String description) {
        this.remoteId = remoteId;
        this.beacon = beacon;
        this.description = description;
    }

    public MyObject(Beacon beacon, String description) {
        this(null, beacon, description);
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
    }

    public int getBeaconId() {
        return beacon.getId();
    }

    public String getBeaconRemoteId(){
        return beacon.getRemoteId();
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return beacon.getUuid();
    }

    public String getDisplayableMajor() {
        return beacon.getDisplayableMajor();
    }

    public String getDisplayableMinor() {
        return beacon.getDisplayableMinor();
    }

    public JsonObject createJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(MyObject.BEACON_ID, beacon.getRemoteId());
        jsonObject.put(MyObject.DESCRIPTION, description);
        return jsonObject;
    }

    public String getSearchPredictionText() {
        return description;
    }

    public CharSequence getMyObjectConvertToString(String myObjectAutoCompleteTextView) {
        return description;
    }

    public int getId() {
        return id;
    }
}
