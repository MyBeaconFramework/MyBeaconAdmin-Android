package com.brokoli.mybeaconadmin.view.myobject;

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
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.manager.MyObjectManager;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.utils.AdapterUtils;
import com.brokoli.mybeaconadmin.utils.Validator;
import com.brokoli.mybeaconadmin.view.custom.CursomOrmLiteCursorAdapter;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;

public class AddObjectActivity extends ActionBarActivity {
    /*
     * Attributes
     */

    // Views
    private EditText objectEditTextDescription;
    private AutoCompleteTextView beaconAutoCompleteTextView;

    // Attributes
    private int selectedBeacon = -1;

    /*
     * Activity lifecycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_object);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        objectEditTextDescription = (EditText) findViewById(R.id.objectEditTextDescription);
        beaconAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.beaconAutoCompleteTextView);
        setupAutoCompleteTextViews();
    }

    private void setupAutoCompleteTextViews() {
        final CursomOrmLiteCursorAdapter<Beacon, View> adapter1 = AdapterUtils.getBeaconAdapter(this, beaconAutoCompleteTextView,
                new int[]{Beacon.OBJECT_BEACON_TYPE});
        beaconAutoCompleteTextView.setAdapter(adapter1);
        beaconAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeacon = (int) id;
            }
        });
    }

    public void addObject(View view) {
        if(validate()){
            Beacon beacon = DatabaseHelper.getHelper(this).findBeacon(selectedBeacon);
            if(beacon == null){
                beaconAutoCompleteTextView.setError(getString(R.string.notFound));
                beaconAutoCompleteTextView.requestFocus();
                return;
            }

            MyObject myObject = new MyObject(beacon, objectEditTextDescription.getText().toString());
            MyObjectManager.getInstance().createMyObjectAndUploadToServer(this, myObject, true);

            finish();
        }
    }

    private boolean validate(){
        boolean ret = true;

        ret = Validator.validate(objectEditTextDescription, ret);

        if(selectedBeacon == -1){
            beaconAutoCompleteTextView.setError(getString(R.string.mandatory));
            if(ret){
                beaconAutoCompleteTextView.requestFocus();
            }
            ret = false;
        }
        return ret;
    }
}
