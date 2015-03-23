package com.brokoli.mybeaconadmin.view.user;

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
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.view.logs.LogsActivity;
import com.j256.ormlite.android.apptools.OrmLiteCursorAdapter;
import com.j256.ormlite.android.apptools.support.OrmLiteCursorLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public class UserActivity extends ActionBarActivity {
    /*
     * Attributes
     */

    //DAOs
    private Dao<User, Integer> userIntegerDao;

    // Loader
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RuntimeExceptionDao<User, Integer> userIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(this).getUserIntegerRuntimeExceptionDao();
        QueryBuilder<User, Integer> userIntegerQueryBuilder = userIntegerRuntimeExceptionDao.queryBuilder();
        userIntegerQueryBuilder.orderBy(User.USERNAME, true);

        final PreparedQuery<User> preparedQuery;
        try {
            userIntegerDao = DatabaseHelper.getHelper(this).getUserIntegerDao();
            preparedQuery = userIntegerQueryBuilder.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        final OrmLiteCursorAdapter<User, View> adapter = new OrmLiteCursorAdapter<User, View>(this) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.user_row, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, User user) {
                TextView userUsernameTextView = (TextView) itemView.findViewById(R.id.userUsernameTextView);
                TextView userDescriptionTextView = (TextView) itemView.findViewById(R.id.userDescriptionTextView);
                userUsernameTextView.setText(user.getUsername());
                userDescriptionTextView.setText(user.getDescription());
            }
        };

        ListView listUsers = (ListView) findViewById(R.id.listUsers);
        listUsers.setAdapter(adapter);

        View listUsersEmptyView = findViewById(R.id.listUsersEmptyView);
        listUsers.setEmptyView(listUsersEmptyView);

        listUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), UserActionsActivity.class);
                intent.putExtra(UserActionsActivity.USER_ID_KEY, (int) id);
                startActivity(intent);
                return true;
            }
        });

        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), LogsActivity.class);
                intent.putExtra(LogsActivity.EXTRA_USER_ID, (int)id);
                startActivity(intent);
            }
        });

        loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                return new OrmLiteCursorLoader<>(UserActivity.this, userIntegerDao, preparedQuery);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                adapter.changeCursor(cursor, ((OrmLiteCursorLoader<User>) loader).getQuery());
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapter.changeCursor(null, null);
            }
        };
        getSupportLoaderManager().initLoader(0, null, loaderCallbacks);
    }
}
