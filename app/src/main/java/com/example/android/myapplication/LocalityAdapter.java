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
 * Created by mayank raj sinha on 01-04-2017.
 */

public class LocalityAdapter extends ArrayAdapter {
    public LocalityAdapter(@NonNull Context context, @NonNull ArrayList<badassclass> arrayList) {
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            //  listItemView=mInflater.inflate(R.layout.earthquake_list,false);


            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_layout, parent, false);
        }
        badassclass currentclass= (badassclass) getItem(position);
        TextView locality_textview= (TextView) listItemView.findViewById(R.id.locality_name);
        locality_textview.setText(currentclass.getMlocality_name());
        TextView locality_textview_number= (TextView) listItemView.findViewById(R.id.locality_name_number);
   //     locality_textview_number.setText(currentclass.getMnumber());
        return listItemView;

    }
}
