package com.brokoli.mybeaconadmin.data;

import com.baasbox.android.json.JsonObject;
import com.j256.ormlite.field.DatabaseField;

public class Gate {
    public static final String REMOTE_ID = "remoteId";
    public static final String DESCRIPTION = "description";
    public static final String BEACON_A_ID = "beaconAId";
    public static final String BEACON_B_ID = "beaconBId";

    public static final String BEACON_A_ID_DB = "beaconA_id";
    public static final String BEACON_B_ID_DB = "beaconB_id";

    @DatabaseField(generatedId = true, columnName = "_id")
    private int id;

    @DatabaseField
    private String remoteId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Beacon beaconA;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Beacon beaconB;

    @DatabaseField
    private String description;

    public Gate(){

    }

    public Gate(String remoteId, Beacon beaconA, Beacon beaconB, String description){
        this.remoteId = remoteId;
        this.beaconA = beaconA;
        this.beaconB = beaconB;
        this.description = description;
    }

    public Gate(Beacon beaconA, Beacon beaconB, String description){
        this(null, beaconA, beaconB, description);
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

    public Beacon getBeaconA() {
        return beaconA;
    }

    public Beacon getBeaconB() {
        return beaconB;
    }

    public JsonObject createJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Gate.BEACON_A_ID, beaconA.getRemoteId());
        jsonObject.put(Gate.BEACON_B_ID, beaconB.getRemoteId());
        jsonObject.put(Gate.DESCRIPTION, description);
        return jsonObject;
    }

    public boolean isEqualTo(String remoteId) {
        return this.remoteId.equals(remoteId);
    }

    public String getSearchPredictionText() {
        return description;
    }

    public CharSequence getGateConvertToString(String gateAutoCompleteTextView) {
        return description;
    }

    public int getId() {
        return id;
    }
}
