package com.swu.kalkulator;

public class Riwayat {
    private final String perhitungan;
    private final String hasil;

    public Riwayat(String perhitungan, String hasil) {
        this.perhitungan = perhitungan;
        this.hasil = hasil;
    }

    public String getPerhitungan() {
        return perhitungan;
    }

    public String getHasil() {
        return hasil;
    }
}