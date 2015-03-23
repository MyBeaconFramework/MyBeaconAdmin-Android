package com.brokoli.mybeaconadmin.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.MyApp;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;

import java.util.List;

public class MyBeaconManager {
    public static final String CREATE_BEACON_ENDPOINT = "plugin/document.Beacon";

    private static MyBeaconManager ourInstance = new MyBeaconManager();

    public static MyBeaconManager getInstance() {
        return ourInstance;
    }

    public boolean getAllBeaconsFromRemoteAndUpdateLocalThenUpdateRemote(Context context) {
        // Get all my beacons from remote
        BaasResult<List<BaasDocument>> listBaasResult = BaasDocument.fetchAllSync(MainController.BEACON_COLLECTION);
        if (listBaasResult.isSuccess()) {
            // Update local
            // For every remote MyBeacon
            for (BaasDocument doc : listBaasResult.value()) {
                String remoteId = doc.getId();
                int remoteBeaconType = doc.getInt(Beacon.BEACON_TYPE, -1);
                String remoteUUID = doc.getString(Beacon.UUID_FIELD);
                int remoteMajor = doc.getInt(Beacon.MAJOR, -1);
                int remoteMinor = doc.getInt(Beacon.MINOR, -1);
                String remoteDescription = doc.getString(Beacon.DESCRIPTION);
                double remoteLatitude = doc.getDouble(Beacon.LATITUDE, -1);
                double remoteLongitude = doc.getDouble(Beacon.LONGITUDE, -1);

                Beacon beacon = new Beacon(remoteId, remoteBeaconType, remoteUUID, remoteMajor, remoteMinor, remoteDescription, remoteLatitude,
                        remoteLongitude);
                DatabaseHelper.getHelper(context).createIfNotExists(beacon);
            }

            // Update remote
            List<Beacon> allNotSyncedBeacons = DatabaseHelper.getHelper(context).getAllNotSyncedMyBeacons();
            for(Beacon beacon : allNotSyncedBeacons){
                uploadBeaconToServer(context, beacon, false);
            }
            return true;
        } else {
            Log.e("LOG", "Error", listBaasResult.error());
            return false;
        }
    }

    public void createBeaconAndUploadToServer(Context context, Beacon beacon, boolean async){
        if(DatabaseHelper.getHelper(context).createIfNotExists(beacon)) {
            uploadBeaconToServer(context, beacon, async);
        } else {
            Toast.makeText(context, "Beacon already exists or other error occurred!", Toast.LENGTH_LONG).show();
        }
    }

    // If async = true, is user initiated; otherwise = isn´t
    private void uploadBeaconToServer(Context context, final Beacon beacon, boolean async) {
        JsonObject jsonObject = beacon.createJsonObject();
        BaasBox box = BaasBox.getDefault();
        if(async) {
            box.rest(HttpRequest.POST, CREATE_BEACON_ENDPOINT, jsonObject, true,
                    new BaasHandler<JsonObject>() {
                        @Override
                        public void handle(BaasResult<JsonObject> res) {
                            handleRes(res, beacon, true);
                        }
                    });
        } else {
            BaasResult<JsonObject> res = box.restSync(HttpRequest.POST, CREATE_BEACON_ENDPOINT, jsonObject, true);
            handleRes(res, beacon, false);
        }
    }

    private void handleRes(BaasResult<JsonObject> res, Beacon beacon, boolean userInitiated){
        if (res.isSuccess()) {
            String remoteId = res.value().getObject("data").getString("id",null);
            Log.d("fadfdas", "remoteId = " + remoteId);
            beacon.setRemoteId(remoteId);
            DatabaseHelper.getHelper(MyApp.getAppContext()).update(beacon);
            Log.d("log", res.toString());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "Beacon added succesfully", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("faddfas", "Error! isSynced = false, need to send another time. " + res.error().getLocalizedMessage());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "Beacon not added!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
