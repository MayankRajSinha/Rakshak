package com.example.android.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mayank raj sinha on 02-04-2017.
 */

public class LocationAdapter extends ArrayAdapter {
    public LocationAdapter(@NonNull Context context, ArrayList<friendsclass> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            //  listItemView=mInflater.inflate(R.layout.earthquake_list,false);


            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.location_listview_layout, parent, false);
        }
        friendsclass currentClass = (friendsclass) getItem(position);
        TextView number = (TextView) listItemView.findViewById(R.id.friends_number);
        number.setText(currentClass.getMnumber());
        TextView address = (TextView) listItemView.findViewById(R.id.address_friend);
        address.setText(currentClass.getMaddress());
        return listItemView;
    }
}
