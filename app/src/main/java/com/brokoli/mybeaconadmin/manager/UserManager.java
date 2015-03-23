package com.brokoli.mybeaconadmin.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.brokoli.mybeaconadmin.MyApp;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;

import java.util.List;

public class UserManager {
    private static final String TAG = "UserManager";

    // URL
    private static final String INDICATE_USER_PASSED_BY_SECTOR_ENDPOINT = "plugin/indicate.UserPassedBySector";
    // Params
    public static final String SECTOR_BEACON_ID = MyObject.BEACON_ID;
    private static final String SECTOR_USERNAME = User.USERNAME;

    // URL
    private static final String INDICATE_USER_PASSED_BY_GATE_ENDPOINT = "plugin/indicate.UserPassedByGate";
    // Params
    private static final String GATE_LAST_GATE_BEACON_ID_ = "lastGateBeaconId";
    private static final String GATE_GATE_ID = "gateId";
    private static final String GATE_USERNAME = User.USERNAME;

    // URL
    private static final String INDICATE_USER_PASSED_BY_OBJECT_ENDPOINT = "plugin/indicate.UserPassedByObject";
    // Params
    private static final String OBJECT_CLOSER_BEACON_SECTOR = "closerBeaconSector";
    private static final String OBJECT_BEACON_LINKED_OBJECT_ID = "beaconLinkedObjectId"; // ID of beacon linked to the object

    private static UserManager ourInstance = new UserManager();

    public static UserManager getInstance() {
        return ourInstance;
    }

    public boolean getAllMyUsersFromRemoteAndUpdateLocal(Context context) {
        BaasResult<List<BaasUser>> res = BaasUser.fetchAllSync();
        if (res.isSuccess()) {
            // Update local
            // For every remote User
            for (BaasUser u : res.value()) {
                String remoteUsername = u.getName();
                String remoteDescription = "";

                User user = new User(remoteUsername, remoteDescription);
                DatabaseHelper.getHelper(context).createIfNotExists(user);
            }
            return true;
        } else {
            Log.e("LOG", "Error", res.error());
            return false;
        }
    }

    /**
     * Indicate that a given user U passed by sector indicated by Beacon B
     */
    public void indicateUserPassedBySectorAsync(Context context, User user, int beaconId){
        Beacon beacon = DatabaseHelper.getHelper(context).getMyBeaconIntegerRuntimeExceptionDao().queryForId(beaconId);
        if(beacon != null){
            if(beacon.isSectorBeacon()){
                BaasBox box = BaasBox.getDefault();

                JsonObject jsonObject = new JsonObject();
                jsonObject.put(SECTOR_BEACON_ID, beacon.getRemoteId());
                jsonObject.put(SECTOR_USERNAME, user.getUsername());

                box.rest(HttpRequest.POST, INDICATE_USER_PASSED_BY_SECTOR_ENDPOINT, jsonObject, true,
                        new BaasHandler<JsonObject>() {
                            @Override
                            public void handle(BaasResult<JsonObject> res) {
                                if (res.isSuccess()) {
                                    Log.d("log", res.toString());
                                    Toast.makeText(MyApp.getAppContext(), "Success!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("faddfas", "Error! need to send another time. " + res.error().getLocalizedMessage());
                                    Toast.makeText(MyApp.getAppContext(), "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(MyApp.getAppContext(), "Beacon deve ser de setor!", Toast.LENGTH_LONG).show();
            }

        } else {
            Log.d(TAG, "beacon is null");
        }
    }

    /**
     * Indicate that a given user U passed by gate G with lastGateBeaconId
     */
    public void indicateUserPassedByGateAsync(Context context, User user, int gateId, int lastGateBeaconId){
        Beacon beacon = DatabaseHelper.getHelper(context).getMyBeaconIntegerRuntimeExceptionDao().queryForId(lastGateBeaconId);
        Gate gate = DatabaseHelper.getHelper(context).getGateIntegerRuntimeExceptionDao().queryForId(gateId);
        if(beacon != null && gate != null){
            if(beacon.isGateBacon()){
                BaasBox box = BaasBox.getDefault();

                JsonObject jsonObject = new JsonObject();
                jsonObject.put(GATE_LAST_GATE_BEACON_ID_, beacon.getRemoteId());
                jsonObject.put(GATE_GATE_ID, gate.getRemoteId());
                jsonObject.put(GATE_USERNAME, user.getUsername());

                box.rest(HttpRequest.POST, INDICATE_USER_PASSED_BY_GATE_ENDPOINT, jsonObject, true,
                        new BaasHandler<JsonObject>() {
                            @Override
                            public void handle(BaasResult<JsonObject> res) {
                                if (res.isSuccess()) {
                                    Log.d("log", res.toString());
                                    Toast.makeText(MyApp.getAppContext(), "Success!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("faddfas", "Error! need to send another time. " + res.error().getLocalizedMessage());
                                    Toast.makeText(MyApp.getAppContext(), "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(MyApp.getAppContext(), "Beacon deve ser de gate!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "beacon is null or gate is null");
        }
    }

    /**
     * Indicate that a MyObject was found and the closer beacon of type Sector was lastGateBeaconId
     */
    public void indicateUserPassedByObjectAsync(Context context, User user, int objectId, int lastGateBeaconId){
        Beacon beacon = DatabaseHelper.getHelper(context).getMyBeaconIntegerRuntimeExceptionDao().queryForId(lastGateBeaconId);
        MyObject myObject = DatabaseHelper.getHelper(context).getMyObjectIntegerRuntimeExceptionDao().queryForId(objectId);
        if(beacon != null && myObject != null){
            if(beacon.isSectorBeacon()){
                BaasBox box = BaasBox.getDefault();

                JsonObject jsonObject = new JsonObject();
                jsonObject.put(OBJECT_CLOSER_BEACON_SECTOR, beacon.getRemoteId());
                jsonObject.put(OBJECT_BEACON_LINKED_OBJECT_ID, myObject.getBeaconRemoteId());
                jsonObject.put(User.USERNAME, user.getUsername());

                box.rest(HttpRequest.POST, INDICATE_USER_PASSED_BY_OBJECT_ENDPOINT, jsonObject, true,
                        new BaasHandler<JsonObject>() {
                            @Override
                            public void handle(BaasResult<JsonObject> res) {
                                if (res.isSuccess()) {
                                    Log.d("log", res.toString());
                                    Toast.makeText(MyApp.getAppContext(), "Success!", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("faddfas", "Error! need to send another time. " + res.error().getLocalizedMessage());
                                    Toast.makeText(MyApp.getAppContext(), "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(MyApp.getAppContext(), "Beacon deve ser de setor!", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "beacon is null or myObject is null");
        }
    }
}
