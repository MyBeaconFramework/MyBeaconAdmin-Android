package com.brokoli.mybeaconadmin.manager;

import android.content.Context;
import android.util.Log;

import com.baasbox.android.BaasDocument;
import com.baasbox.android.BaasResult;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Logger;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.List;

public class LoggerManager {
    private static LoggerManager ourInstance = new LoggerManager();

    public static LoggerManager getInstance() {
        return ourInstance;
    }

    public boolean getAllLogsFromRemoteAndUpdateLocal(Context context) {
        // Get all logs from remote
        BaasResult<List<BaasDocument>> listBaasResult = BaasDocument.fetchAllSync(MainController.LOGGER_COLLECTION);
        if (listBaasResult.isSuccess()) {
            // Update local
            // For every remote Logger
            for (BaasDocument doc : listBaasResult.value()) {
                String remoteId = doc.getId();
                String creationDateString = doc.getCreationDate();
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                DateTime dt = dtf.parseDateTime(creationDateString);
                Date creationDate = dt.toDate();

                int type = doc.getInt(Logger.TYPE, -1);

                Logger logger = null;

                if(type == Logger.USER_PASSED_BY_SECTOR){
                    String username = doc.getString(Logger.USERNAME);
                    String remoteBeaconId = doc.getString(Logger.BEACON_ID);

                    User user = DatabaseHelper.getHelper(context).findUser(username);
                    Beacon beacon = DatabaseHelper.getHelper(context).findBeacon(remoteBeaconId);
                    if(user != null && beacon != null) {
                        logger = new Logger(remoteId, creationDate, user, beacon);
                    } else {
                        Log.d("LOG", "Beacon or User not found remotely! Could not create Logger found on remote locally");
                    }
                } else if(type == Logger.USER_PASSED_BY_GATE){
                    String username = doc.getString(Logger.USERNAME);
                    String remoteGateId = doc.getString(Logger.GATE_ID);
                    String lastGateBeaconId = doc.getString(Logger.LAST_GATE_BEACON_ID);

                    User user = DatabaseHelper.getHelper(context).findUser(username);
                    Gate gate = DatabaseHelper.getHelper(context).findGate(remoteGateId);
                    Beacon beacon = DatabaseHelper.getHelper(context).findBeacon(lastGateBeaconId);
                    if(user != null && gate != null && beacon != null) {
                        logger = new Logger(remoteId, creationDate, user, gate, beacon);
                    } else {
                        Log.d("LOG", "Beacon or Gate or User not found remotely! Could not create Logger found on remote locally");
                    }
                } else if(type == Logger.USER_PASSED_BY_OBJECT){
                    String remoteObjectId = doc.getString(Logger.OBJECT_ID);
                    String lastBeaconId = doc.getString(Logger.LAST_BEACON_ID);

                    MyObject myObject = DatabaseHelper.getHelper(context).findMyObject(remoteObjectId);
                    Beacon beacon = DatabaseHelper.getHelper(context).findBeacon(lastBeaconId);
                    if(myObject != null && beacon != null) {
                        logger = new Logger(remoteId, creationDate, myObject, beacon);
                    } else {
                        Log.d("LOG", "MyObject or Beacon not found remotely! Could not create Logger found on remote locally");
                    }
                } else {
                    Log.d("LOG", "Error! Received unrecognized log type: " + type);
                }

                if(logger != null){
                    DatabaseHelper.getHelper(context).createIfNotExists(logger);
                }
            }
            return true;
        } else {
            Log.e("LOG", "Error", listBaasResult.error());
            return false;
        }
    }
}
