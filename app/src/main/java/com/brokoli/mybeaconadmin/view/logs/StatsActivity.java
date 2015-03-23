package com.brokoli.mybeaconadmin.view.logs;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Logger;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.utils.AdapterUtils;
import com.brokoli.mybeaconadmin.view.custom.CursomOrmLiteCursorAdapter;

public class StatsActivity extends ActionBarActivity {
    /*
     * Attributes
     */

    // Views
    private TextView whereIsUserTextView;
    private TextView whereIsUserGateTextView;
    private TextView whereIsUserGateDirectionTextView;
    private AutoCompleteTextView whereIsUserAutoCompleteTextView;
    private TextView whereIsObjectTextView;
    private AutoCompleteTextView whereIsObjectAutoCompleteTextView;

    // Attributes
    private int selectedUser = -1;
    private int selectedObject = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_ativity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        whereIsUserTextView = (TextView) findViewById(R.id.whereIsUserTextView);
        whereIsUserGateTextView = (TextView) findViewById(R.id.whereIsUserGateTextView);
        whereIsUserGateDirectionTextView = (TextView) findViewById(R.id.whereIsUserGateDirectionTextView);
        whereIsUserAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.whereIsUserAutoCompleteTextView);
        whereIsObjectTextView = (TextView) findViewById(R.id.whereIsObjectTextView);
        whereIsObjectAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.whereIsObjectAutoCompleteTextView);
        setupAutoCompleteTextViews();
    }

    private void setupAutoCompleteTextViews() {
        final CursomOrmLiteCursorAdapter<User, View> adapter1 = AdapterUtils.getUserAdapter(this, whereIsUserAutoCompleteTextView);
        whereIsUserAutoCompleteTextView.setAdapter(adapter1);
        whereIsUserAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedUser = (int) id;
            }
        });

        final CursomOrmLiteCursorAdapter<MyObject, View> adapter2 = AdapterUtils.getMyObjectAdapter(this, whereIsObjectAutoCompleteTextView);
        whereIsObjectAutoCompleteTextView.setAdapter(adapter2);
        whereIsObjectAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedObject = (int) id;
            }
        });
    }

    public void findUser(View view) {
        Logger logger1 = DatabaseHelper.getHelper(this).findLastLocationBeaconFromUser(selectedUser);
        if(logger1 != null){
            whereIsUserTextView.setText(logger1.getBeacon().getMyBeaconRowText() + " | " + logger1.getDisplayableDate());
        } else{
            whereIsUserTextView.setText("Not found");
        }

        Logger logger2 = DatabaseHelper.getHelper(this).findLastLocationGateFromUser(selectedUser);
        if(logger2 != null){
            whereIsUserGateTextView.setText(logger2.getGate().getSearchPredictionText() + " | " + logger2.getDisplayableDate());
            whereIsUserGateDirectionTextView.setText(logger2.getBeacon().getMyBeaconRowText());
        } else{
            whereIsUserGateTextView.setText("Not found");
            whereIsUserGateDirectionTextView.setText("Not found");
        }
    }

    public void findObject(View view) {
        Logger logger = DatabaseHelper.getHelper(this).findLastLocationBeaconFromObject(selectedObject);
        if(logger != null){
            whereIsObjectTextView.setText(logger.getBeacon().getMyBeaconRowText() + " | " + logger.getDisplayableDate());
        } else{
            whereIsObjectTextView.setText("Not found");
        }
    }
}
