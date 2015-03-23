package com.brokoli.mybeaconadmin.view.beacon;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.manager.MyBeaconManager;
import com.brokoli.mybeaconadmin.R;


public class AddBeaconActivity extends ActionBarActivity {
    private EditText editTextUUID, editTextMajor, editTextMinor, editTextLatitude, editTextLongitude;
    private LocationListener locationListener;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextUUID = ((EditText)findViewById(R.id.editTextUUID));
        editTextMajor = ((EditText)findViewById(R.id.editTextMajor));
        editTextMinor = ((EditText)findViewById(R.id.editTextMinor));
        editTextLatitude = ((EditText)findViewById(R.id.editTextLatitude));
        editTextLongitude = ((EditText)findViewById(R.id.editTextLongitude));

        Intent myIntent = getIntent();
        String extraBeaconUUID = myIntent.getStringExtra(BeaconActivity.EXTRA_BEACON_UUID);
        int extraBeaconMajor = myIntent.getIntExtra(BeaconActivity.EXTRA_BEACON_MAJOR, -1);
        int extraBeaconMinor = myIntent.getIntExtra(BeaconActivity.EXTRA_BEACON_MINOR, -1);
        if(!TextUtils.isEmpty(extraBeaconUUID) && extraBeaconMajor != -1 && extraBeaconMinor != -1){
            editTextUUID.setText(extraBeaconUUID);
            editTextMajor.setText(String.valueOf(extraBeaconMajor));
            editTextMinor.setText(String.valueOf(extraBeaconMinor));
        }

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            editTextLatitude.setText(String.valueOf(latitude));
            editTextLongitude.setText(String.valueOf(longitude));
        } else {
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if (TextUtils.isEmpty(editTextLatitude.getText()) && TextUtils.isEmpty(editTextLongitude.getText())) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        editTextLatitude.setText(String.valueOf(latitude));
                        editTextLongitude.setText(String.valueOf(longitude));
                    }
                    lm.removeUpdates(locationListener);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(lm != null && locationListener != null){
            lm.removeUpdates(locationListener);
        }
    }

    public void addBeacon(View view) {
        int beaconType = -1;
        int checkedRadioButtonId = ((RadioGroup)findViewById(R.id.radioGroupBeaconType)).getCheckedRadioButtonId();
        if(checkedRadioButtonId == R.id.radioButtonSector){
            beaconType = Beacon.SECTOR_BEACON_TYPE;
        } else if(checkedRadioButtonId == R.id.radioButtonObject){
            beaconType = Beacon.OBJECT_BEACON_TYPE;
        } else if(checkedRadioButtonId == R.id.radioButtonGate){
            beaconType = Beacon.GATE_BEACON_TYPE;
        } else if(checkedRadioButtonId == R.id.radioButtonGateSector){
            beaconType = Beacon.GATE_SECTOR_BEACON_TYPE;
        }

        if(validate()){
            String UUID = editTextUUID.getText().toString();
            int major = Integer.valueOf(editTextMajor.getText().toString());
            int minor = Integer.valueOf(editTextMinor.getText().toString());

            String description = ((EditText)findViewById(R.id.editTextDescription)).getText().toString();

            String latitudeString = ((EditText) findViewById(R.id.editTextLatitude)).getText().toString();
            double latitude;
            if(TextUtils.isEmpty(latitudeString)){
                latitude = Beacon.NO_LATITUDE;
            } else {
                latitude = Double.valueOf(latitudeString);
            }

            String longitudeString = ((EditText) findViewById(R.id.editTextLongitude)).getText().toString();
            double longitude;
            if(TextUtils.isEmpty(longitudeString)){
                longitude = Beacon.NO_LONGITUDE;
            } else {
                longitude = Double.valueOf(longitudeString);
            }

            Beacon beacon = new Beacon(beaconType, UUID, major, minor, description, latitude, longitude);
            MyBeaconManager.getInstance().createBeaconAndUploadToServer(this, beacon, true);

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validate(){
        boolean ret = true;
        ret = validate(editTextUUID, ret);
        ret = validate(editTextMajor, ret);
        ret = validate(editTextMinor, ret);
        return ret;
    }

    private boolean validate(EditText editText, boolean ret) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError(getString(R.string.obrigatorio));
            if (ret) {
                editText.requestFocus();
            }
            return false;
        }
        return ret;
    }
}
