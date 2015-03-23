package com.brokoli.mybeaconadmin.view.beacon;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.view.logs.LogsActivity;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;


public class BeaconActivity extends ActionBarActivity {
    public static final String EXTRA_BEACON_UUID = "extraBeaconUUID";
    public static final String EXTRA_BEACON_MAJOR = "extraBeaconMajor";
    public static final String EXTRA_BEACON_MINOR = "extraBeaconMinor";
    private String extraBeaconUUID;
    private int extraBeaconMajor;
    private int extraBeaconMinor;

    /*
     * Attributes
     */

    //DAOs
    private Dao<Beacon, Integer> myBeaconIntegerDao;

    // Loader
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        extraBeaconUUID = myIntent.getStringExtra(EXTRA_BEACON_UUID);
        extraBeaconMajor = myIntent.getIntExtra(EXTRA_BEACON_MAJOR, -1);
        extraBeaconMinor = myIntent.getIntExtra(EXTRA_BEACON_MINOR, -1);

        RuntimeExceptionDao<Beacon, Integer> myBeaconIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(this).getMyBeaconIntegerRuntimeExceptionDao();
        QueryBuilder<Beacon, Integer> myBeaconIntegerQueryBuilder = myBeaconIntegerRuntimeExceptionDao.queryBuilder();
        myBeaconIntegerQueryBuilder.orderBy(Beacon.UUID_FIELD, true);
        myBeaconIntegerQueryBuilder.orderBy(Beacon.MAJOR, true);
        myBeaconIntegerQueryBuilder.orderBy(Beacon.MINOR, true);

        final PreparedQuery<Beacon> preparedQuery;
        try {
            if(!TextUtils.isEmpty(extraBeaconUUID) && extraBeaconMajor != -1 && extraBeaconMinor != -1){
                myBeaconIntegerQueryBuilder.where().eq(Beacon.UUID_FIELD, extraBeaconUUID).and().eq(Beacon.MAJOR, extraBeaconMajor).and().
                        eq(Beacon.MINOR, extraBeaconMinor);
            }
            myBeaconIntegerDao = DatabaseHelper.getHelper(this).getMyBeaconIntegerDao();
            preparedQuery = myBeaconIntegerQueryBuilder.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final OrmLiteCursorAdapter<Beacon, View> adapter = new OrmLiteCursorAdapter<Beacon, View>(this) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.my_beacon_row, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, Beacon myBeacon) {
                TextView textViewDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
                TextView textViewUUID = (TextView) itemView.findViewById(R.id.textViewUUID);
                TextView textViewMajor = (TextView) itemView.findViewById(R.id.textViewMajor);
                TextView textViewMinor = (TextView) itemView.findViewById(R.id.textViewMinor);

                textViewDescription.setText(myBeacon.getDescription());
                textViewUUID.setText(myBeacon.getUuid());
                textViewMajor.setText(myBeacon.getDisplayableMajor());
                textViewMinor.setText(myBeacon.getDisplayableMinor());

                if(myBeacon.getRemoteId() != null){
                    textViewDescription.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                }
                else {
                    textViewDescription.setTextColor(getResources().getColor(R.color.holo_red_dark));
                }
            }
        };

        ListView listBeacons = (ListView) findViewById(R.id.listBeacons);
        listBeacons.setAdapter(adapter);

        View listBeaconsEmptyView = findViewById(R.id.listBeaconsEmptyView);
        listBeacons.setEmptyView(listBeaconsEmptyView);

        listBeacons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), LogsActivity.class);
                intent.putExtra(LogsActivity.EXTRA_BEACON_ID, (int)id);
                startActivity(intent);
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<>(BeaconActivity.this, myBeaconIntegerDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<Beacon>) loader).getQuery());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_add_beacon:
                Intent intent = new Intent(this, AddBeaconActivity.class);
                if(!TextUtils.isEmpty(extraBeaconUUID) && extraBeaconMajor != -1 && extraBeaconMinor != -1){
                    intent.putExtra(EXTRA_BEACON_UUID, extraBeaconUUID);
                    intent.putExtra(EXTRA_BEACON_MAJOR, extraBeaconMajor);
                    intent.putExtra(EXTRA_BEACON_MINOR, extraBeaconMinor);
                }
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
