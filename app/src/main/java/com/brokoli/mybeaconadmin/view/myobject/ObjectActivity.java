package com.brokoli.mybeaconadmin.view.myobject;

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
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.view.logs.LogsActivity;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class ObjectActivity extends ActionBarActivity {
/*
     * Attributes
     */

    //DAOs
    private Dao<MyObject, Integer> myObjectIntegerDao;

    // Loader
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RuntimeExceptionDao<MyObject, Integer> myObjectIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(this).getMyObjectIntegerRuntimeExceptionDao();
        QueryBuilder<MyObject, Integer> myObjectIntegerQueryBuilder = myObjectIntegerRuntimeExceptionDao.queryBuilder();
        myObjectIntegerQueryBuilder.orderBy(MyObject.DESCRIPTION, true);

        final PreparedQuery<MyObject> preparedQuery;
        try {
            myObjectIntegerDao = DatabaseHelper.getHelper(this).getMyObjectIntegerDao();
            preparedQuery = myObjectIntegerQueryBuilder.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final OrmLiteCursorAdapter<MyObject, View> adapter = new OrmLiteCursorAdapter<MyObject, View>(this) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.my_object_row, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, MyObject myObject) {
                TextView myObjectDescription = (TextView) itemView.findViewById(R.id.myObjectDescription);
                TextView myObjectUUIDTextView = (TextView) itemView.findViewById(R.id.myObjectUUIDTextView);
                TextView myObjectMajorTextView = (TextView) itemView.findViewById(R.id.myObjectMajorTextView);
                TextView myObjectMinorTextView = (TextView) itemView.findViewById(R.id.myObjectMinorTextView);

                myObjectDescription.setText(myObject.getDescription());
                myObjectUUIDTextView.setText(myObject.getUuid());
                myObjectMajorTextView.setText(myObject.getDisplayableMajor());
                myObjectMinorTextView.setText(myObject.getDisplayableMinor());

                if(myObject.getRemoteId() != null){
                    myObjectDescription.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                }
                else {
                    myObjectDescription.setTextColor(getResources().getColor(R.color.holo_red_dark));
                }
            }
        };

        ListView listObjects = (ListView) findViewById(R.id.listObjects);
        listObjects.setAdapter(adapter);

        View listObjectsEmptyView = findViewById(R.id.listObjectsEmptyView);
        listObjects.setEmptyView(listObjectsEmptyView);

        listObjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), LogsActivity.class);
                intent.putExtra(LogsActivity.EXTRA_MY_OBJECT_ID, (int)id);
                startActivity(intent);
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<>(ObjectActivity.this, myObjectIntegerDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<MyObject>) loader).getQuery());
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
        getMenuInflater().inflate(R.menu.menu_object, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_add_object:
                Intent intent = new Intent(this, AddObjectActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
