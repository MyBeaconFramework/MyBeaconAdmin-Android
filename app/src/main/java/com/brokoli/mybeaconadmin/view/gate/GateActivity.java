package com.brokoli.mybeaconadmin.view.gate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.view.logs.LogsActivity;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class GateActivity extends ActionBarActivity {
    /*
     * Attributes
     */

    //DAOs
    private Dao<Gate, Integer> gateIntegerDao;

    // Loader
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RuntimeExceptionDao<Gate, Integer> gateIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(this).getGateIntegerRuntimeExceptionDao();
        QueryBuilder<Gate, Integer> gateIntegerQueryBuilder = gateIntegerRuntimeExceptionDao.queryBuilder();
        gateIntegerQueryBuilder.orderBy(Gate.DESCRIPTION, true);

        final PreparedQuery<Gate> preparedQuery;
        try {
            gateIntegerDao = DatabaseHelper.getHelper(this).getGateIntegerDao();
            preparedQuery = gateIntegerQueryBuilder.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final OrmLiteCursorAdapter<Gate, View> adapter = new OrmLiteCursorAdapter<Gate, View>(this) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.gate_row, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, Gate gate) {
                TextView gateDescription = (TextView) itemView.findViewById(R.id.gateDescription);
                TextView beaconAUUID = (TextView) itemView.findViewById(R.id.beaconAUUID);
                TextView beaconBUUID = (TextView) itemView.findViewById(R.id.beaconBUUID);
                TextView beaconAMajor = (TextView) itemView.findViewById(R.id.beaconAMajor);
                TextView beaconBMajor = (TextView) itemView.findViewById(R.id.beaconBMajor);
                TextView beaconAMinor = (TextView) itemView.findViewById(R.id.beaconAMinor);
                TextView beaconBMinor = (TextView) itemView.findViewById(R.id.beaconBMinor);

                gateDescription.setText(gate.getDescription());
                beaconAUUID.setText(gate.getBeaconA().getUuid());
                beaconBUUID.setText(gate.getBeaconB().getUuid());
                beaconAMajor.setText(gate.getBeaconA().getDisplayableMajor());
                beaconBMajor.setText(gate.getBeaconB().getDisplayableMajor());
                beaconAMinor.setText(gate.getBeaconA().getDisplayableMinor());
                beaconBMinor.setText(gate.getBeaconB().getDisplayableMinor());

                if(gate.getRemoteId() != null){
                    gateDescription.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                }
                else {
                    gateDescription.setTextColor(getResources().getColor(R.color.holo_red_dark));
                }
            }
        };

        ListView listGates = (ListView) findViewById(R.id.listGates);
        listGates.setAdapter(adapter);

        View listGatesEmptyView = findViewById(R.id.listGatesEmptyView);
        listGates.setEmptyView(listGatesEmptyView);

        listGates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), LogsActivity.class);
                intent.putExtra(LogsActivity.EXTRA_GATE_ID, (int)id);
                startActivity(intent);
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<>(GateActivity.this, gateIntegerDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<Gate>) loader).getQuery());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.changeCursor(null, null);
            }
        };
        getSupportLoaderManager().initLoader(0, null, loaderCallbacks);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_add_gate:
                Intent intent = new Intent(this, AddGateActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
