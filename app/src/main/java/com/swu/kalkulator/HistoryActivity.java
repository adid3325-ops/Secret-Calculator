package com.swu.kalkulator;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ListView listView = findViewById(R.id.listRiwayat);
        ArrayList<Riwayat> data = new ArrayList<>();

        String isi = getSharedPreferences(
                "KalkulatorData",
                MODE_PRIVATE
        ).getString("riwayat", "");

        if (!isi.isEmpty()) {
            String[] baris = isi.split("\n");

            for (String item : baris) {
                String[] bagian = item.split("\\|", 2);

                if (bagian.length == 2) {
                    data.add(new Riwayat(
                            bagian[0],
                            bagian[1]
                    ));
                }
            }
        }

        RiwayatAdapter adapter =
                new RiwayatAdapter(this, data);

        listView.setAdapter(adapter);

        if (data.isEmpty()) {
            TextView kosong = findViewById(R.id.tvKosong);

            if (kosong != null) {
                kosong.setVisibility(TextView.VISIBLE);
                kosong.setText("Belum ada riwayat perhitungan.");
            }
        }
    }
}