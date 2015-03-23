package com.brokoli.mybeaconadmin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.brokoli.mybeaconadmin.view.realbeacon.TimedBeaconSimulator;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

public class MyApp extends Application {
    private static final String BAASBOX_URL = "put_here_baasbox_url";
    private static final int PORT = 80;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final String APP_CODE = "put_here_app_code";

    private static final String ADMIN_USERNAME = "put_here_admin_username";
    private static final String ADMIN_PASSWORD = "put_here_admin_password";

    private static final boolean SIMULATE_BEACONS = false;

    // App context
    private static Context context;

    public static Context getAppContext() {
        return MyApp.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.context = getApplicationContext();
        BaasBox.Builder b =
                new BaasBox.Builder(this);
        b.setApiDomain(BAASBOX_URL)
                .setAppCode(APP_CODE)
                .setPort(PORT)
                .setUseHttps(false)
                .setHttpSocketTimeout(SOCKET_TIMEOUT)
                .init();

        if (BaasUser.current() == null) {
            loginAsAdmin();
        }
        else{
            Log.d("LOG", "The user is currently logged in: " + BaasUser.current());
        }

        setupBeacon();
    }

    @SuppressWarnings("unused")
    private BackgroundPowerSaver backgroundPowerSaver;
    private void setupBeacon() {
        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        backgroundPowerSaver = new BackgroundPowerSaver(this);

        if(SIMULATE_BEACONS) {
            BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
            ((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
        }
    }

    public void loginAsAdmin(){
        BaasUser user = BaasUser.withUserName(ADMIN_USERNAME)
                .setPassword(ADMIN_PASSWORD);
        user.login(new BaasHandler<BaasUser>() {
            @Override
            public void handle(BaasResult<BaasUser> result) {
                if(result.isSuccess()) {
                    Log.d("LOG", "The user is currently logged in: "+result.value());
                } else {
                    Log.e("LOG","Show error",result.error());
                }
            }
        });
    }
}