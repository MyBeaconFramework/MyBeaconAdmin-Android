package com.brokoli.mybeaconadmin.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brokoli.mybeaconadmin.manager.MainController;
import com.brokoli.mybeaconadmin.manager.MyBeaconManager;
import com.brokoli.mybeaconadmin.MyApp;
import com.brokoli.mybeaconadmin.R;
import com.brokoli.mybeaconadmin.persistence.DatabaseHelper;
import com.brokoli.mybeaconadmin.view.beacon.BeaconActivity;
import com.brokoli.mybeaconadmin.view.gate.GateActivity;
import com.brokoli.mybeaconadmin.view.logs.LogsActivity;
import com.brokoli.mybeaconadmin.view.logs.StatsActivity;
import com.brokoli.mybeaconadmin.view.myobject.ObjectActivity;
import com.brokoli.mybeaconadmin.view.realbeacon.RealBeaconActivity;
import com.brokoli.mybeaconadmin.view.user.UserActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear_all:
                DatabaseHelper.getHelper(this).clearAll();
                Toast.makeText(MyApp.getAppContext(), "Dados limpos!", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_synchronize:
                synchronize();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void seeBeacons(View view) {
        Intent intent = new Intent(this, BeaconActivity.class);
        startActivity(intent);
    }

    public void seeGates(View view) {
        Intent intent = new Intent(this, GateActivity.class);
        startActivity(intent);
    }

    public void seeUsers(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void seeObjects(View view) {
        Intent intent = new Intent(this, ObjectActivity.class);
        startActivity(intent);
    }

    public void seeLogs(View view) {
        Intent intent = new Intent(this, LogsActivity.class);
        startActivity(intent);
    }

    public void seeStats(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }

    public void seeRealBeacon(View view) {
        Intent intent = new Intent(this, RealBeaconActivity.class);
        startActivity(intent);
    }

    public void synchronize() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return MainController.getInstance().synchronize(MainActivity.this);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean) {
                    Toast.makeText(MyApp.getAppContext(), "Synchronized succesfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MyApp.getAppContext(), "Synchronized failed", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
