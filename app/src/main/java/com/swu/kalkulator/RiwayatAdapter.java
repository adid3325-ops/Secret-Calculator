package com.swu.kalkulator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RiwayatAdapter extends ArrayAdapter<Riwayat> {

    public RiwayatAdapter(Context context, ArrayList<Riwayat> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_riwayat, parent, false);
        }

        Riwayat riwayat = getItem(position);

        TextView tvPerhitungan =
                convertView.findViewById(R.id.tvPerhitungan);

        TextView tvHasil =
                convertView.findViewById(R.id.tvHasil);

        if (riwayat != null) {
            tvPerhitungan.setText(riwayat.getPerhitungan());
            tvHasil.setText("Hasil: " + riwayat.getHasil());
        }

        return convertView;
    }
}