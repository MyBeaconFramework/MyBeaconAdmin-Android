package com.brokoli.mybeaconadmin.manager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.net.HttpRequest;
import com.brokoli.mybeaconadmin.MyApp;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;

import java.util.List;

public class GateManager {
    private static final String TAG = "GateManager";
    private static final String CREATE_GATE_ENDPOINT = "plugin/document.Gate";
    private static GateManager ourInstance = new GateManager();

    public static GateManager getInstance() {
        return ourInstance;
    }

    public boolean getAllGatesFromRemoteAndUpdateLocalThenUpdateRemote(Context context) {
        // Get all gates from remote
        BaasResult<List<BaasDocument>> listBaasResult = BaasDocument.fetchAllSync(MainController.GATE_COLLECTION);
        if (listBaasResult.isSuccess()) {
            // Update local
            // For every remote Gate
            for (BaasDocument doc : listBaasResult.value()) {
                String remoteId = doc.getId();
                String remoteDescription = doc.getString(Gate.DESCRIPTION);
                String remoteBeaconAId = doc.getString(Gate.BEACON_A_ID);
                String remoteBeaconBId = doc.getString(Gate.BEACON_B_ID);

                Beacon beaconA = DatabaseHelper.getHelper(context).findBeacon(remoteBeaconAId);
                Beacon beaconB = DatabaseHelper.getHelper(context).findBeacon(remoteBeaconBId);
                if(beaconA != null && beaconB != null) {
                    Gate gate = new Gate(remoteId, beaconA, beaconB, remoteDescription);
                    DatabaseHelper.getHelper(context).createIfNotExists(gate);
                } else {
                    Log.d(TAG, "Beacon A or Beacon B not found remotely! Could not create gate found on remote locally");
                }
            }

            // Update remote
            List<Gate> allNotSyncedMyBeacons = DatabaseHelper.getHelper(context).getAllNotSyncedGates();
            for(Gate gate : allNotSyncedMyBeacons){
                uploadGateToServer(context, gate, false);
            }
            return true;
        } else {
            Log.e("LOG", "Error", listBaasResult.error());
            return false;
        }
    }

    public void createGateAndUploadToServer(Context context, Gate gate, boolean async) {
        if(DatabaseHelper.getHelper(context).createIfNotExists(gate)) {
            uploadGateToServer(context, gate, async);
        } else {
            Toast.makeText(context, "Gate already exists or other error occurred!", Toast.LENGTH_LONG).show();
        }
    }

    // If async = true, is user initiated; otherwise = isn´t
    private void uploadGateToServer(Context context, final Gate gate, boolean async) {
        JsonObject jsonObject = gate.createJsonObject();
        BaasBox box = BaasBox.getDefault();

        if(async) {
            box.rest(HttpRequest.POST, CREATE_GATE_ENDPOINT, jsonObject, true,
                    new BaasHandler<JsonObject>() {
                        @Override
                        public void handle(BaasResult<JsonObject> res) {
                            handleRes(res, gate, true);
                        }
                    });
        } else {
            BaasResult<JsonObject> res = box.restSync(HttpRequest.POST, CREATE_GATE_ENDPOINT, jsonObject, true);
            handleRes(res, gate, false);
        }
    }

    private void handleRes(BaasResult<JsonObject> res, Gate gate, boolean userInitiated){
        if (res.isSuccess()) {
            String remoteId = res.value().getObject("data").getString("id",null);
            gate.setRemoteId(remoteId);
            DatabaseHelper.getHelper(MyApp.getAppContext()).update(gate);
            Log.d("log", res.toString());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "Gate added succesfully", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("faddfas", "Error! isSynced = false, need to send another time. " + res.error().getLocalizedMessage());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "Gate not added!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
