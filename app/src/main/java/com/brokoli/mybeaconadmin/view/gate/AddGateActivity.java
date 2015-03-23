package com.brokoli.mybeaconadmin.view.gate;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.manager.GateManager;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.utils.AdapterUtils;
import com.brokoli.mybeaconadmin.utils.Validator;
import com.brokoli.mybeaconadmin.view.custom.CursomOrmLiteCursorAdapter;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;

public class AddGateActivity extends ActionBarActivity {
    /*
     * Attributes
     */

    // Views
    private EditText gateEditTextDescription;
    private AutoCompleteTextView beaconAAutoCompleteTextView;
    private AutoCompleteTextView beaconBAutoCompleteTextView;

    // Attributes
    private int selectedBeaconA = -1;
    private int selectedBeaconB = -1;

    /*
     * Activity lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gateEditTextDescription = (EditText) findViewById(R.id.gateEditTextDescription);
        beaconAAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.beaconAAutoCompleteTextView);
        beaconBAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.beaconBAutoCompleteTextView);
        setupAutoCompleteTextViews();
    }

    private void setupAutoCompleteTextViews() {
        final CursomOrmLiteCursorAdapter<Beacon, View> adapter1 = AdapterUtils.getBeaconAdapter(this, beaconAAutoCompleteTextView,
                new int[]{Beacon.GATE_BEACON_TYPE, Beacon.GATE_SECTOR_BEACON_TYPE});
        beaconAAutoCompleteTextView.setAdapter(adapter1);
        beaconAAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeaconA = (int) id;
            }
        });

        final CursomOrmLiteCursorAdapter<Beacon, View> adapter2 = AdapterUtils.getBeaconAdapter(this, beaconBAutoCompleteTextView,
                new int[]{Beacon.GATE_BEACON_TYPE, Beacon.GATE_SECTOR_BEACON_TYPE});
        beaconBAutoCompleteTextView.setAdapter(adapter2);
        beaconBAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeaconB = (int) id;
            }
        });
    }

    public void addGate(View view) {
        if(validate()){
            Beacon beaconA = DatabaseHelper.getHelper(this).findBeacon(selectedBeaconA);
            if(beaconA == null){
                beaconAAutoCompleteTextView.setError(getString(R.string.notFound));
                beaconAAutoCompleteTextView.requestFocus();
                return;
            }

            Beacon beaconB = DatabaseHelper.getHelper(this).findBeacon(selectedBeaconB);
            if(beaconB == null){
                beaconBAutoCompleteTextView.setError(getString(R.string.notFound));
                beaconBAutoCompleteTextView.requestFocus();
                return;
            }

            Gate gate = new Gate(beaconA, beaconB, gateEditTextDescription.getText().toString());
            GateManager.getInstance().createGateAndUploadToServer(this, gate, true);

            finish();
        }
    }

    private boolean validate(){
        boolean ret = true;

        ret = Validator.validate(gateEditTextDescription,ret);

        if(selectedBeaconA == -1){
            beaconAAutoCompleteTextView.setError(getString(R.string.mandatory));
            if(ret){
                beaconAAutoCompleteTextView.requestFocus();
            }
            ret = false;
        }
        if(selectedBeaconB == -1){
            beaconBAutoCompleteTextView.setError(getString(R.string.mandatory));
            if(ret){
                beaconBAutoCompleteTextView.requestFocus();
            }
            ret = false;
        }
        return ret;
    }


}
