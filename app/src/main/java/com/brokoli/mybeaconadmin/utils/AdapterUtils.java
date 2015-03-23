package com.brokoli.mybeaconadmin.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.data.Beacon;
import com.brokoli.mybeaconadmin.data.Gate;
import com.brokoli.mybeaconadmin.data.MyObject;
import com.brokoli.mybeaconadmin.data.User;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.view.custom.CursomOrmLiteCursorAdapter;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.StatementBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;

public class AdapterUtils {
    public static CursomOrmLiteCursorAdapter<Beacon, View> getBeaconAdapter(Context context, final AutoCompleteTextView autoCompleteTextView){
        return getBeaconAdapter(context, autoCompleteTextView, null);
    }

    public static CursomOrmLiteCursorAdapter<Beacon, View> getBeaconAdapter(Context context, final AutoCompleteTextView autoCompleteTextView, final int[] beaconsType){
        return new CursomOrmLiteCursorAdapter<Beacon, View>(context) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.my_beacon_row_autocompletetextview, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, Beacon myBeacon) {
                TextView clientRowTextView = (TextView) itemView.findViewById(R.id.myBeaconRowTextView);
                clientRowTextView.setText(myBeacon.getMyBeaconRowText());
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                RuntimeExceptionDao<Beacon, Integer> myBeaconIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(mContext)
                        .getMyBeaconIntegerRuntimeExceptionDao();
                QueryBuilder<Beacon, Integer> myBeaconIntegerQueryBuilder = myBeaconIntegerRuntimeExceptionDao.queryBuilder();
                myBeaconIntegerQueryBuilder.orderBy(Beacon.UUID_FIELD, true);
                myBeaconIntegerQueryBuilder.orderBy(Beacon.MAJOR, true);
                myBeaconIntegerQueryBuilder.orderBy(Beacon.MINOR, true);

                final PreparedQuery<Beacon> preparedQuery;
                try {
                    String searchFilter = "";
                    if (!TextUtils.isEmpty(constraint)) {
                        searchFilter = constraint.toString().trim();
                    }
                    SelectArg selectArg1 = new SelectArg("%" + searchFilter + "%");
                    SelectArg selectArg2 = new SelectArg("%" + searchFilter + "%");
                    SelectArg selectArg3 = new SelectArg("%" + searchFilter + "%");
                    SelectArg selectArg4 = new SelectArg("%" + searchFilter + "%");

                    Where<Beacon, Integer> where = myBeaconIntegerQueryBuilder.where();
                    where.like(Beacon.DESCRIPTION, selectArg1);
                    where.like(Beacon.UUID_FIELD, selectArg2);
                    where.like(Beacon.MAJOR, selectArg3);
                    where.like(Beacon.MINOR, selectArg4);
                    where.or(4); // this does an OR between the previous 4 clauses, it also puts a clause back on the stack

                    if(beaconsType != null && beaconsType.length > 0){
                        // http://stackoverflow.com/questions/23270641/ormlite-parenthesis-in-join-where-clauses

                        for(int beaconType : beaconsType){
                            where.eq(Beacon.BEACON_TYPE, beaconType);
                        }

                        if(beaconsType.length > 1){
                            where.or(beaconsType.length);
                        }

                        // this does an AND between the previous 2 OR clauses
                        where.and(2);
                    }
                    preparedQuery = myBeaconIntegerQueryBuilder.prepare();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Set prepared query used internally
                setPreparedQuery(preparedQuery);
                return getCursorFromPreparedQuery(myBeaconIntegerRuntimeExceptionDao);
            }

            //you need to override this to return the string value when selecting an item from the autocomplete suggestions
            @Override
            public CharSequence convertToString(Cursor cursor) {
                try {
                    return cursorToObject(cursor).getAddGateConvertToString(autoCompleteTextView.getText().toString().toLowerCase());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }

    public static CursomOrmLiteCursorAdapter<Gate, View> getGateAdapter(Context context, final AutoCompleteTextView autoCompleteTextView){
        return new CursomOrmLiteCursorAdapter<Gate, View>(context) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.generic_autocompletetextview, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, Gate gate) {
                TextView clientRowTextView = (TextView) itemView.findViewById(R.id.genericRowTextView);
                clientRowTextView.setText(gate.getSearchPredictionText());
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                RuntimeExceptionDao<Gate, Integer> gateIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(mContext).getGateIntegerRuntimeExceptionDao();
                QueryBuilder<Gate, Integer> gateIntegerQueryBuilder = gateIntegerRuntimeExceptionDao.queryBuilder();
                gateIntegerQueryBuilder.orderBy(Gate.DESCRIPTION, true);

                final PreparedQuery<Gate> preparedQuery;
                try {
                    String searchFilter = "";
                    if (!TextUtils.isEmpty(constraint)) {
                        searchFilter = constraint.toString().trim();
                    }
                    SelectArg selectArg = new SelectArg("%" + searchFilter + "%");
                    gateIntegerQueryBuilder.where().like(Gate.DESCRIPTION, selectArg);
                    preparedQuery = gateIntegerQueryBuilder.prepare();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Set prepared query used internally
                setPreparedQuery(preparedQuery);
                return getCursorFromPreparedQuery(gateIntegerRuntimeExceptionDao);
            }

            //you need to override this to return the string value when selecting an item from the autocomplete suggestions
            @Override
            public CharSequence convertToString(Cursor cursor) {
                try {
                    return cursorToObject(cursor).getGateConvertToString(autoCompleteTextView.getText().toString().toLowerCase());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }

    public static CursomOrmLiteCursorAdapter<MyObject, View> getMyObjectAdapter(Context context, final AutoCompleteTextView autoCompleteTextView){
        return new CursomOrmLiteCursorAdapter<MyObject, View>(context) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.generic_autocompletetextview, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, MyObject myObject) {
                TextView clientRowTextView = (TextView) itemView.findViewById(R.id.genericRowTextView);
                clientRowTextView.setText(myObject.getSearchPredictionText());
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                RuntimeExceptionDao<MyObject, Integer> myObjectIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(mContext).getMyObjectIntegerRuntimeExceptionDao();
                QueryBuilder<MyObject, Integer> myObjectIntegerQueryBuilder = myObjectIntegerRuntimeExceptionDao.queryBuilder();
                myObjectIntegerQueryBuilder.orderBy(MyObject.DESCRIPTION, true);

                final PreparedQuery<MyObject> preparedQuery;
                try {
                    String searchFilter = "";
                    if (!TextUtils.isEmpty(constraint)) {
                        searchFilter = constraint.toString().trim();
                    }
                    SelectArg selectArg = new SelectArg("%" + searchFilter + "%");
                    myObjectIntegerQueryBuilder.where().like(MyObject.DESCRIPTION, selectArg);
                    preparedQuery = myObjectIntegerQueryBuilder.prepare();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Set prepared query used internally
                setPreparedQuery(preparedQuery);
                return getCursorFromPreparedQuery(myObjectIntegerRuntimeExceptionDao);
            }

            //you need to override this to return the string value when selecting an item from the autocomplete suggestions
            @Override
            public CharSequence convertToString(Cursor cursor) {
                try {
                    return cursorToObject(cursor).getMyObjectConvertToString(autoCompleteTextView.getText().toString().toLowerCase());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }

    public static CursomOrmLiteCursorAdapter<User, View> getUserAdapter(Context context, final AutoCompleteTextView autoCompleteTextView){
        return new CursomOrmLiteCursorAdapter<User, View>(context) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                LayoutInflater inflater = LayoutInflater.from(context);
                return inflater.inflate(R.layout.generic_autocompletetextview, viewGroup, false);
            }

            @Override
            public void bindView(View itemView, Context context, User user) {
                TextView clientRowTextView = (TextView) itemView.findViewById(R.id.genericRowTextView);
                clientRowTextView.setText(user.getUsername());
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                RuntimeExceptionDao<User, Integer> userIntegerRuntimeExceptionDao = DatabaseHelper.getHelper(mContext).getUserIntegerRuntimeExceptionDao();
                QueryBuilder<User, Integer> userIntegerQueryBuilder = userIntegerRuntimeExceptionDao.queryBuilder();
                userIntegerQueryBuilder.orderBy(User.USERNAME, true);

                final PreparedQuery<User> preparedQuery;
                try {
                    String searchFilter = "";
                    if (!TextUtils.isEmpty(constraint)) {
                        searchFilter = constraint.toString().trim();
                    }
                    SelectArg selectArg = new SelectArg("%" + searchFilter + "%");
                    userIntegerQueryBuilder.where().like(User.USERNAME, selectArg);
                    preparedQuery = userIntegerQueryBuilder.prepare();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Set prepared query used internally
                setPreparedQuery(preparedQuery);
                return getCursorFromPreparedQuery(userIntegerRuntimeExceptionDao);
            }

            //you need to override this to return the string value when selecting an item from the autocomplete suggestions
            @Override
            public CharSequence convertToString(Cursor cursor) {
                try {
                    return cursorToObject(cursor).getUsername();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return "";
            }
        };
    }
}
