package com.brokoli.mybeaconadmin.view.user;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.manager.UserManager;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.utils.AdapterUtils;
import com.brokoli.mybeaconadmin.view.custom.CursomOrmLiteCursorAdapter;

public class UserActionsActivity extends ActionBarActivity {
    public static final String USER_ID_KEY = "userIdKey";
    /*
     * Attributes
     */

    // Views
    private AutoCompleteTextView setorAutoCompleteTextView;
    private AutoCompleteTextView gateAutoCompleteTextView;
    private AutoCompleteTextView objectAutoCompleteTextView;

    // Attributes
    private int selectedBeacon = -1;
    private int selectedGate = -1;
    private int selectedObject = -1;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_actions);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setorAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.setorAutoCompleteTextView);
        gateAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.gateAutoCompleteTextView);
        objectAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.objectAutoCompleteTextView);

        Intent myIntent = getIntent();
        int userID = myIntent.getIntExtra(USER_ID_KEY, -1);
        user = DatabaseHelper.getHelper(this).getUserIntegerRuntimeExceptionDao().queryForId(userID);
        if(user == null){
            // show error message
        } else {
            setupAutoCompleteTextViews();
        }
    }

    private void setupAutoCompleteTextViews() {
        setupBeaconAutoCompleteTextView();
        setupGateAutoCompleteTextView();
        setupObjectAutoCompleteTextView();
    }

    private void setupBeaconAutoCompleteTextView() {
        final CursomOrmLiteCursorAdapter<Beacon, View> adapter1 = AdapterUtils.getBeaconAdapter(this, setorAutoCompleteTextView);
        setorAutoCompleteTextView.setAdapter(adapter1);
        setorAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBeacon = (int) id;
            }
        });
    }

    private void setupGateAutoCompleteTextView() {
        final CursomOrmLiteCursorAdapter<Gate, View> adapter2 = AdapterUtils.getGateAdapter(this, gateAutoCompleteTextView);
        gateAutoCompleteTextView.setAdapter(adapter2);
        gateAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGate = (int) id;
            }
        });
    }

    private void setupObjectAutoCompleteTextView() {
        final CursomOrmLiteCursorAdapter<MyObject, View> adapter3 = AdapterUtils.getMyObjectAdapter(this, objectAutoCompleteTextView);
        objectAutoCompleteTextView.setAdapter(adapter3);
        objectAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedObject = (int) id;
            }
        });
    }

    /**
     * Button click events
     */

    public void passedBySector(View view) {
        if(selectedBeacon == -1){
            setorAutoCompleteTextView.setError(getString(R.string.mandatory));
            setorAutoCompleteTextView.requestFocus();
        } else if(user != null) {
            UserManager.getInstance().indicateUserPassedBySectorAsync(this, user, selectedBeacon);
        }
    }

    public void passedByGate(View view) {
        if(selectedBeacon == -1){
            setorAutoCompleteTextView.setError(getString(R.string.mandatory));
            setorAutoCompleteTextView.requestFocus();
        } else if(selectedGate == -1) {
            gateAutoCompleteTextView.setError(getString(R.string.mandatory));
            gateAutoCompleteTextView.requestFocus();
        } else if(user != null) {
            UserManager.getInstance().indicateUserPassedByGateAsync(this, user, selectedGate, selectedBeacon);
        }
    }

    public void passedByObject(View view) {
        if(selectedBeacon == -1){
            setorAutoCompleteTextView.setError(getString(R.string.mandatory));
            setorAutoCompleteTextView.requestFocus();
        } else if(selectedObject == -1) {
            objectAutoCompleteTextView.setError(getString(R.string.mandatory));
            objectAutoCompleteTextView.requestFocus();
        } else if(user != null) {
            UserManager.getInstance().indicateUserPassedByObjectAsync(this, user, selectedObject, selectedBeacon);
        }
    }
}
