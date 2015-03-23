package com.brokoli.mybeaconadmin.manager;

import android.content.Context;

public class MainController {
    public static final String BEACON_COLLECTION = "Beacon";
    public static final String GATE_COLLECTION = "Gate";
    public static final String MY_OBJECT_COLLECTION = "Object";
    public static final String LOGGER_COLLECTION = "Logger";

    private static MainController ourInstance = new MainController();

    public static MainController getInstance() {
        return ourInstance;
    }

    public boolean synchronize(Context context) {
        //noinspection SimplifiableIfStatement
        if(!MyBeaconManager.getInstance().getAllBeaconsFromRemoteAndUpdateLocalThenUpdateRemote(context)){
            return false;
        }
        //noinspection SimplifiableIfStatement
        if(!GateManager.getInstance().getAllGatesFromRemoteAndUpdateLocalThenUpdateRemote(context)){
            return false;
        }
        //noinspection SimplifiableIfStatement
        if(!UserManager.getInstance().getAllMyUsersFromRemoteAndUpdateLocal(context)){
            return false;
        }
        //noinspection SimplifiableIfStatement
        if(!MyObjectManager.getInstance().getAllObjectsFromRemoteAndUpdateLocalThenUpdateRemote(context)){
            return false;
        }
        return LoggerManager.getInstance().getAllLogsFromRemoteAndUpdateLocal(context);
    }
}
