package com.swu.kalkulator;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
        implements SensorEventListener {

    EditText editNum1, editNum2;
    Button buttonPlus, buttonMin, buttonMulti, buttonDiv;
    Button buttonCalc, buttonClear;
    TextView textResult;

    String operatorTerpilih = "";
    double angka1, angka2, hasil;

    LocationManager locationManager;
    SensorManager sensorManager;
    Sensor accelerometer, gyroscope;

    TextView textSensor;
    AlertDialog dialogSensor;

    float ax, ay, az;
    float gx, gy, gz;

    boolean adaAccelerometer = false;
    boolean adaGyroscope = false;

    private final ActivityResultLauncher<Intent> kameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> Toast.makeText(
                            this,
                            "Kamera selesai dibuka.",
                            Toast.LENGTH_SHORT
                    ).show()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNum1 = findViewById(R.id.editNum1);
        editNum2 = findViewById(R.id.editNum2);
        buttonPlus = findViewById(R.id.buttonPlus);
        buttonMin = findViewById(R.id.buttonMin);
        buttonMulti = findViewById(R.id.buttonMulti);
        buttonDiv = findViewById(R.id.buttonDiv);
        buttonCalc = findViewById(R.id.buttonCalc);
        buttonClear = findViewById(R.id.buttonClear);
        textResult = findViewById(R.id.textResult);

        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
        );

        gyroscope = sensorManager.getDefaultSensor(
                Sensor.TYPE_GYROSCOPE
        );

        buttonPlus.setOnClickListener(v -> pilihOperator("+"));
        buttonMin.setOnClickListener(v -> pilihOperator("-"));
        buttonMulti.setOnClickListener(v -> pilihOperator("*"));
        buttonDiv.setOnClickListener(v -> pilihOperator("/"));

        buttonCalc.setOnClickListener(v -> hitung());

        buttonClear.setOnClickListener(v -> {
            editNum1.setText("");
            editNum2.setText("");
            textResult.setText("Hasil:");
            operatorTerpilih = "";
        });
    }

    private void pilihOperator(String operator) {
        operatorTerpilih = operator;
    }

    private void hitung() {
        String input1 = editNum1.getText().toString().trim();
        String input2 = editNum2.getText().toString().trim();

        if (input1.isEmpty() || input2.isEmpty()) {
            Toast.makeText(this, "Masukkan kedua angka.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (operatorTerpilih.isEmpty()) {
            Toast.makeText(this, "Pilih operator terlebih dahulu.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            angka1 = Double.parseDouble(input1);
            angka2 = Double.parseDouble(input2);

            switch (operatorTerpilih) {
                case "+":
                    hasil = angka1 + angka2;
                    break;

                case "-":
                    hasil = angka1 - angka2;
                    break;

                case "*":
                    hasil = angka1 * angka2;
                    break;

                case "/":
                    if (angka2 == 0) {
                        Toast.makeText(this, "Tidak bisa dibagi nol.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    hasil = angka1 / angka2;
                    break;

                default:
                    return;
            }

            String hasilTeks =
                    new DecimalFormat("0.##########").format(hasil);

            textResult.setText("Hasil: " + hasilTeks);

            simpanRiwayat(
                    input1 + " " + operatorTerpilih + " " + input2,
                    hasilTeks
            );

            if (Math.abs(hasil) < 0.000000001) {
                bukaKamera();

            } else if (Math.abs(hasil - 3) < 0.000000001) {
                putarAudio();

            } else if (Math.abs(hasil - 7) < 0.000000001) {
                bukaGPS();

            } else if (Math.abs(hasil - 8) < 0.000000001) {
                pilihFiturDelapan();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Angka tidak valid.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void simpanRiwayat(String perhitungan, String hasil) {
        SharedPreferences prefs =
                getSharedPreferences("KalkulatorData", MODE_PRIVATE);

        String lama = prefs.getString("riwayat", "");
        String baru = perhitungan + "|" + hasil;

        if (!lama.isEmpty()) {
            baru = lama + "\n" + baru;
        }

        prefs.edit().putString("riwayat", baru).apply();
    }

    private void bukaKamera() {
        Intent intent =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            kameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Kamera tidak ditemukan.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void putarAudio() {
        MediaPlayer player = MediaPlayer.create(this, R.raw.suara);

        if (player != null) {
            player.setOnCompletionListener(MediaPlayer::release);
            player.start();
        } else {
            Toast.makeText(this, "Audio tidak bisa diputar.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void bukaGPS() {
        boolean izinFine = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean izinCoarse = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (!izinFine && !izinCoarse) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 101);
            return;
        }

        ambilLokasi();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == 101) {
            boolean diizinkan = false;

            for (int hasilIzin : grantResults) {
                if (hasilIzin == PackageManager.PERMISSION_GRANTED) {
                    diizinkan = true;
                    break;
                }
            }

            if (diizinkan) {
                bukaGPS();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ambilLokasi() {
        boolean izinFine = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        boolean izinCoarse = ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        if (!izinFine && !izinCoarse) {
            return;
        }

        try {
            Criteria criteria = new Criteria();
            String provider =
                    locationManager.getBestProvider(criteria, true);

            if (provider == null) {
                Toast.makeText(this, "Aktifkan GPS terlebih dahulu.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Mencari lokasi...",
                    Toast.LENGTH_SHORT).show();

            locationManager.requestSingleUpdate(
                    provider,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location lokasi) {
                            double latitude = lokasi.getLatitude();
                            double longitude = lokasi.getLongitude();

                            Uri uri = Uri.parse(
                                    "geo:" + latitude + "," + longitude
                                            + "?q=" + latitude + "," + longitude
                            );

                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW, uri);

                            intent.setPackage(
                                    "com.google.android.apps.maps");

                            try {
                                startActivity(intent);
                            } catch (Exception e) {
                                intent.setPackage(null);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }

                        @Override
                        public void onStatusChanged(
                                String provider, int status, Bundle extras) {
                        }
                    },
                    Looper.getMainLooper()
            );

        } catch (SecurityException e) {
            Toast.makeText(this, "Izin lokasi belum tersedia.",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal mengambil lokasi.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void pilihFiturDelapan() {
        String[] pilihan = {
                "Tampilkan gambar kucing",
                "Buka sensor"
        };

        new AlertDialog.Builder(this)
                .setTitle("Pilih fitur")
                .setItems(pilihan, (dialog, nomor) -> {
                    if (nomor == 0) {
                        tampilkanGambar();
                    } else {
                        bukaSensor();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void tampilkanGambar() {
        ImageView gambar = new ImageView(this);
        gambar.setImageResource(R.drawable.meme_kucing);
        gambar.setAdjustViewBounds(true);
        gambar.setScaleType(ImageView.ScaleType.FIT_CENTER);

        new AlertDialog.Builder(this)
                .setTitle("Meme Kucing")
                .setView(gambar)
                .setPositiveButton("Tutup", null)
                .show();
    }

    private void bukaSensor() {
        adaAccelerometer = accelerometer != null;
        adaGyroscope = gyroscope != null;

        if (!adaAccelerometer && !adaGyroscope) {
            Toast.makeText(this, "Sensor tidak tersedia.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        textSensor = new TextView(this);
        textSensor.setPadding(32, 24, 32, 24);
        textSensor.setTextSize(16);
        perbaruiTampilanSensor();

        dialogSensor = new AlertDialog.Builder(this)
                .setTitle("Sensor HP")
                .setView(textSensor)
                .setPositiveButton("Tutup", (dialog, which) -> {
                    sensorManager.unregisterListener(this);
                })
                .create();

        dialogSensor.setOnDismissListener(dialog ->
                sensorManager.unregisterListener(this));

        dialogSensor.show();

        if (adaAccelerometer) {
            sensorManager.registerListener(
                    this, accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (adaGyroscope) {
            sensorManager.registerListener(
                    this, gyroscope,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void perbaruiTampilanSensor() {
        if (textSensor == null) {
            return;
        }

        String data = "";

        if (adaAccelerometer) {
            data += "AKSELEROMETER\n"
                    + "X: " + ax + "\n"
                    + "Y: " + ay + "\n"
                    + "Z: " + az + "\n\n";
        }

        if (adaGyroscope) {
            data += "GIROSKOP\n"
                    + "X: " + gx + "\n"
                    + "Y: " + gy + "\n"
                    + "Z: " + gz;
        }

        textSensor.setText(data);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
        }

        perbaruiTampilanSensor();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}