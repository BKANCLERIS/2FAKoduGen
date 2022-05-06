package com.example.a2fa;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class listviewAdapteris extends ArrayAdapter<elementas> {


    public listviewAdapteris(ArrayList<elementas> data, int resourse, Context context) { super(context, resourse, data); }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) { convertView = LayoutInflater.from(getContext()).inflate(R.layout.element_cell, parent, false);}
        TextView textviewpavadinimas = (TextView) convertView.findViewById(R.id.elementasPavadinimas);
        TextView textviewone = (TextView) convertView.findViewById(R.id.elementasonetimepassword);
        elementas item = getItem(position);
        textviewpavadinimas.setText(item.getissuer());
        textviewone.setText(item.getOnetimepassword());

        return convertView;
    }
}

