package com.brokoli.mybeaconadmin.view.logs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.Logger;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

public class LogsActivity extends ActionBarActivity {
    public static final String EXTRA_TYPE = "extraType";
    public static final String EXTRA_USER_ID = "extraUserId";
    public static final String EXTRA_BEACON_ID = "extraBeaconId";
    public static final String EXTRA_GATE_ID = "extraGateId";
    public static final String EXTRA_MY_OBJECT_ID = "extraMyObjectId";

    /*
     * Attributes
     */

    //DAOs
    private Dao<Logger, Integer> loggerIntegerDao;

    // Loader
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent myIntent = getIntent();
        int type = myIntent.getIntExtra(EXTRA_TYPE, -1);
        int userId = myIntent.getIntExtra(EXTRA_USER_ID, -1);
        int beaconId = myIntent.getIntExtra(EXTRA_BEACON_ID, -1);
        int gateId = myIntent.getIntExtra(EXTRA_GATE_ID, -1);
        int myObjectId = myIntent.getIntExtra(EXTRA_MY_OBJECT_ID, -1);


        RuntimeExceptionDao<Logger, Integer> loggerIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(this).getLoggerIntegerRuntimeExceptionDao();
        QueryBuilder<Logger, Integer> loggerIntegerQueryBuilder = loggerIntegerRuntimeExceptionDao.queryBuilder();
        loggerIntegerQueryBuilder.orderBy(Logger.TYPE, true);
        loggerIntegerQueryBuilder.orderBy(Logger.DATE, false);

        final PreparedQuery<Logger> preparedQuery;
        try {
            int count = 0;
            Where<Logger, Integer> where = loggerIntegerQueryBuilder.where();
            if(type != -1){
                where.eq(Logger.TYPE, type);
                count++;
            }
            if(userId != -1){
                where.eq(Logger.USER_ID_DB, userId);
                count++;
            }
            if(beaconId != -1){
                where.eq(Logger.BEACON_ID_DB, beaconId);
                count++;
            }
            if(gateId != -1){
                where.eq(Logger.GATE_ID_DB, gateId);
                count++;
            }
            if(myObjectId != -1){
                where.eq(Logger.MY_OBJECT_ID_DB, myObjectId);
                count++;
            }

            if(count > 1){
                where.and(count);
            }
            if(count > 0) {
                loggerIntegerQueryBuilder.setWhere(where);
            } else{
                loggerIntegerQueryBuilder.setWhere(null);
            }

            loggerIntegerDao = DatabaseHelper.getHelper(this).getLoggerIntegerDao();
            preparedQuery = loggerIntegerQueryBuilder.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final OrmLiteCursorAdapter<Logger, View> adapter = new OrmLiteCursorAdapter<Logger, View>(this) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.log_row, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, Logger logger) {
                TextView typeTextView = (TextView) itemView.findViewById(R.id.typeTextView);
                TextView info1TextView = (TextView) itemView.findViewById(R.id.info1TextView);
                TextView info2TextView = (TextView) itemView.findViewById(R.id.info2TextView);
                TextView info3TextView = (TextView) itemView.findViewById(R.id.info3TextView);
                TextView dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
                typeTextView.setText(logger.getDisplayableType());
                info1TextView.setText(logger.getInfo1());
                info2TextView.setText(logger.getInfo2());
                String info3 = logger.getInfo3();
                if(!TextUtils.isEmpty(info3)){
                    info3TextView.setVisibility(View.VISIBLE);
                    info3TextView.setText(info3);
                } else {
                    info3TextView.setVisibility(View.GONE);
                }
                dateTextView.setText(logger.getDisplayableDate());
            }
        };

        ListView listLogs = (ListView) findViewById(R.id.listLogs);
        listLogs.setAdapter(adapter);

        View listLogsEmptyView = findViewById(R.id.listLogsEmptyView);
        listLogs.setEmptyView(listLogsEmptyView);

        listLogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger logger = DatabaseHelper.getHelper(getBaseContext()).findLogger((int)id);
                if(logger != null) {
                    Intent intent = new Intent(getBaseContext(), LogsActivity.class);
                    intent.putExtra(LogsActivity.EXTRA_TYPE, logger.getType());
                    User user = logger.getUser();
                    if(user != null){
                        intent.putExtra(LogsActivity.EXTRA_USER_ID, user.getId());
                    }
                    Beacon beacon = logger.getBeacon();
                    if(beacon != null){
                        intent.putExtra(LogsActivity.EXTRA_BEACON_ID, beacon.getId());
                    }
                    Gate gate = logger.getGate();
                    if(gate != null){
                        intent.putExtra(LogsActivity.EXTRA_GATE_ID, gate.getId());
                    }
                    MyObject myObject = logger.getMyObject();
                    if(myObject != null){
                        intent.putExtra(LogsActivity.EXTRA_MY_OBJECT_ID, myObject.getId());
                    }
                    startActivity(intent);
                }
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<>(LogsActivity.this, loggerIntegerDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<Logger>) loader).getQuery());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.changeCursor(null, null);
            }
        };
        getSupportLoaderManager().initLoader(0, null, loaderCallbacks);
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
}
