package com.brokoli.mybeaconadmin.view.custom;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;

public abstract class CursomOrmLiteCursorAdapter<T, ViewType extends View> extends CursorAdapter {
    protected PreparedQuery<T> preparedQuery;

    public CursomOrmLiteCursorAdapter(Context context) {
        super(context, null, false);
    }

    /**
     * Bind the view to a particular item.
     */
    public abstract void bindView(ViewType itemView, Context context, T item);

    /**
     * Final to prevent subclasses from accidentally overriding. Intentional overriding can be accomplished by
     * overriding {@link #doBindView(android.view.View, android.content.Context, android.database.Cursor)}.
     *
     * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
     */
    @Override
    public final void bindView(View itemView, Context context, Cursor cursor) {
        doBindView(itemView, context, cursor);
    }

    /**
     * This is here to make sure that the user really wants to override it.
     */
    protected void doBindView(View itemView, Context context, Cursor cursor) {
        try {
            @SuppressWarnings("unchecked")
            ViewType itemViewType = (ViewType) itemView;
            bindView(itemViewType, context, cursorToObject(cursor));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Map a single row to our cursor object.
     */
    protected T cursorToObject(Cursor cursor) throws SQLException {
        return preparedQuery.mapRow(new AndroidDatabaseResults(cursor, null));
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
    }

    public void setPreparedQuery(PreparedQuery<T> preparedQuery) {
        this.preparedQuery = preparedQuery;
    }

    public Cursor getCursorFromPreparedQuery(RuntimeExceptionDao<T, ?> runtimeExceptionDao) {
        Cursor cursor;
        try {
            DatabaseConnection connection = runtimeExceptionDao.getConnectionSource().getReadOnlyConnection();
            AndroidCompiledStatement statement = (AndroidCompiledStatement) preparedQuery.compile(connection, com.j256.ormlite.stmt.StatementBuilder.StatementType.SELECT);
            cursor = statement.getCursor();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // fill the cursor with results
        cursor.getCount();
        return cursor;
    }
}
