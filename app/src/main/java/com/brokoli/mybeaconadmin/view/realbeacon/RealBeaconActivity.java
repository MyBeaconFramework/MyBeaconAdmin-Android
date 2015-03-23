package com.brokoli.mybeaconadmin.view.realbeacon;

import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.view.beacon.BeaconActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RealBeaconActivity extends ActionBarActivity implements BeaconConsumer {
    private static final String TAG = "RealBeaconActivity";
    private BeaconManager beaconManager;
    private List<Beacon> beaconList;
    private BeaconArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_beacon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listRealBeacons = (ListView) findViewById(R.id.listRealBeacons);
        View listRealBeaconsEmptyView = findViewById(R.id.listRealBeaconsEmptyView);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        beaconList = new ArrayList<>();
        adapter = new BeaconArrayAdapter(this, beaconList);
        listRealBeacons.setAdapter(adapter);
        listRealBeacons.setEmptyView(listRealBeaconsEmptyView);

        listRealBeacons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Beacon selectedBeacon = beaconList.get((int)id);
                if(selectedBeacon != null){
                    Intent intent = new Intent(getBaseContext(), BeaconActivity.class);
                    intent.putExtra(BeaconActivity.EXTRA_BEACON_UUID, selectedBeacon.getId1().toString());
                    intent.putExtra(BeaconActivity.EXTRA_BEACON_MAJOR, selectedBeacon.getId2().toInt());
                    intent.putExtra(BeaconActivity.EXTRA_BEACON_MINOR, selectedBeacon.getId3().toInt());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beaconsRanged, Region region) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Beacon> listBeaconsRanged = new ArrayList<Beacon>(beaconsRanged);

                        List<Beacon> listBeaconsToRemove = new ArrayList<Beacon>();
                        for(Beacon beacon : beaconList){
                            boolean found = false;

                            for(Beacon beaconRanged : listBeaconsRanged){
                                if(beacon.getId1().equals(beaconRanged.getId1()) &&
                                        beacon.getId2().equals(beaconRanged.getId2()) &&
                                        beacon.getId3().equals(beaconRanged.getId3())){
                                    found = true;
                                }
                            }
                            if(!found){
                                listBeaconsToRemove.add(beacon);
                            }
                        }
                        beaconList.removeAll(listBeaconsToRemove);

                        for(Beacon beaconRanged : listBeaconsRanged){
                            boolean found = false;

                            for(Beacon beacon : beaconList){
                                if(beacon.getId1().equals(beaconRanged.getId1()) &&
                                        beacon.getId2().equals(beaconRanged.getId2()) &&
                                        beacon.getId3().equals(beaconRanged.getId3())){
                                    found = true;
                                }
                            }
                            if(!found){
                                beaconList.add(beaconRanged);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6", null, null, null));
        } catch (RemoteException e) {    }
    }
}
