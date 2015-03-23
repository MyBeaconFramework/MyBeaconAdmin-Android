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
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;

import java.util.List;

public class MyObjectManager {
    private static final String TAG = "MyObjectManager";
    private static final String CREATE_OBJECT_ENDPOINT = "plugin/document.Object";

    private static MyObjectManager ourInstance = new MyObjectManager();

    public static MyObjectManager getInstance() {
        return ourInstance;
    }

    public boolean getAllObjectsFromRemoteAndUpdateLocalThenUpdateRemote(Context context) {
        // Get all my objects from remote
        BaasResult<List<BaasDocument>> listBaasResult = BaasDocument.fetchAllSync(MainController.MY_OBJECT_COLLECTION);
        if (listBaasResult.isSuccess()) {
            // Update local
            // For every remote MyObject
            for (BaasDocument doc : listBaasResult.value()) {
                String remoteId = doc.getId();
                String remoteDescription = doc.getString(MyObject.DESCRIPTION);
                String remoteBeaconId = doc.getString(MyObject.BEACON_ID);

                Beacon beacon = DatabaseHelper.getHelper(context).findBeacon(remoteBeaconId);
                if(beacon != null) {
                    MyObject myObject = new MyObject(remoteId, beacon, remoteDescription);
                    DatabaseHelper.getHelper(context).createIfNotExists(myObject);
                } else {
                    Log.d(TAG, "Beacon not found! Could not create MyObject found on remote locally");
                }
            }

            // Update remote
            List<MyObject> allNotSyncedMyObjects = DatabaseHelper.getHelper(context).getAllNotSyncedMyObjects();
            for(MyObject myObject : allNotSyncedMyObjects){
                uploadMyObjectToServer(context, myObject, false);
            }
            return true;
        } else {
            Log.e("LOG", "Error", listBaasResult.error());
            return false;
        }
    }

    public void createMyObjectAndUploadToServer(Context context, MyObject myObject, boolean async) {
        if(DatabaseHelper.getHelper(context).createIfNotExists(myObject)) {
            uploadMyObjectToServer(context, myObject, async);
        } else {
            Toast.makeText(context, "MyObject already exists or other error occurred!", Toast.LENGTH_LONG).show();
        }
    }

    // If async = true, is user initiated; otherwise = isn´t
    private void uploadMyObjectToServer(Context context, final MyObject myObject, boolean async) {
        JsonObject jsonObject = myObject.createJsonObject();
        BaasBox box = BaasBox.getDefault();

        if(async) {
            box.rest(HttpRequest.POST, CREATE_OBJECT_ENDPOINT, jsonObject, true,
                    new BaasHandler<JsonObject>() {
                        @Override
                        public void handle(BaasResult<JsonObject> res) {
                            handleRes(res, myObject, true);
                        }
                    });
        } else {
            BaasResult<JsonObject> res = box.restSync(HttpRequest.POST, CREATE_OBJECT_ENDPOINT, jsonObject, true);
            handleRes(res, myObject, false);
        }
    }

    private void handleRes(BaasResult<JsonObject> res, MyObject myObject, boolean userInitiated){
        if (res.isSuccess()) {
            String remoteId = res.value().getObject("data").getString("id",null);
            myObject.setRemoteId(remoteId);
            DatabaseHelper.getHelper(MyApp.getAppContext()).update(myObject);
            Log.d("log", res.toString());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "MyObject added succesfully", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e("faddfas", "Error! isSynced = false, need to send another time. " + res.error().getLocalizedMessage());

            if(userInitiated) {
                Toast.makeText(MyApp.getAppContext(), "MyObject not added!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
