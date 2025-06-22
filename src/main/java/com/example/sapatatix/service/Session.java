package com.example.sapatatix.service;

public class Session {
    // User ID yang login
    private static String currentUserId;

    // Data profil yang akan diingat selama aplikasi masih berjalan
    private static String namaDepan;
    private static String namaBelakang;
    private static String telepon;
    private static String alamat;
    private static String kota;

    // Getter & Setter untuk userId
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    // Getter & Setter untuk data profil
    public static String getNamaDepan() {
        return namaDepan;
    }

    public static void setNamaDepan(String namaDepan) {
        Session.namaDepan = namaDepan;
    }

    public static String getNamaBelakang() {
        return namaBelakang;
    }

    public static void setNamaBelakang(String namaBelakang) {
        Session.namaBelakang = namaBelakang;
    }

    public static String getTelepon() {
        return telepon;
    }

    public static void setTelepon(String telepon) {
        Session.telepon = telepon;
    }

    public static String getAlamat() {
        return alamat;
    }

    public static void setAlamat(String alamat) {
        Session.alamat = alamat;
    }

    public static String getKota() {
        return kota;
    }

    public static void setKota(String kota) {
        Session.kota = kota;
    }
}
