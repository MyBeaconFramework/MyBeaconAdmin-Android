package com.brokoli.mybeaconadmin.view.realbeacon;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brokoli.mybeaconadmin.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class BeaconArrayAdapter extends ArrayAdapter<Beacon> {
    private final Activity context;
    private final List<Beacon> beaconList;

    static class ViewHolder {
        public TextView textViewDescription, textViewUUID, textViewMajor, textViewMinor;
    }

    public BeaconArrayAdapter(Activity context, List<Beacon> beaconList) {
        super(context, R.layout.my_beacon_row, beaconList);
        this.context = context;
        this.beaconList = beaconList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.my_beacon_row, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
            viewHolder.textViewUUID = (TextView) rowView.findViewById(R.id.textViewUUID);
            viewHolder.textViewMajor = (TextView) rowView.findViewById(R.id.textViewMajor);
            viewHolder.textViewMinor = (TextView) rowView.findViewById(R.id.textViewMinor);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        Beacon beacon = beaconList.get(position);
        holder.textViewDescription.setVisibility(View.GONE);
        holder.textViewUUID.setText(beacon.getId1().toString());
        holder.textViewMajor.setText(beacon.getId2().toString());
        holder.textViewMinor.setText(beacon.getId3().toString());

        return rowView;
    }
}
