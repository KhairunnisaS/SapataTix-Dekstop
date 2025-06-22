package com.example.sapatatix.service;

public class Session {
    // User ID yang login
    private static String currentUserId;

    // Data profil pengguna
    private static String namaDepan;
    private static String namaBelakang;
    private static String telepon;
    private static String alamat;
    private static String kota;

    // Path lokal atau URL foto profil
    private static String fotoProfilPath;

    // Getter & Setter untuk userId
    public static void setCurrentUserId(String userId) {
        currentUserId = userId;
    }

    public static String getCurrentUserId() {
        return currentUserId;
    }

    // Getter & Setter untuk nama depan
    public static String getNamaDepan() {
        return namaDepan;
    }

    public static void setNamaDepan(String namaDepan) {
        Session.namaDepan = namaDepan;
    }

    // Getter & Setter untuk nama belakang
    public static String getNamaBelakang() {
        return namaBelakang;
    }

    public static void setNamaBelakang(String namaBelakang) {
        Session.namaBelakang = namaBelakang;
    }

    // Getter & Setter untuk telepon
    public static String getTelepon() {
        return telepon;
    }

    public static void setTelepon(String telepon) {
        Session.telepon = telepon;
    }

    // Getter & Setter untuk alamat
    public static String getAlamat() {
        return alamat;
    }

    public static void setAlamat(String alamat) {
        Session.alamat = alamat;
    }

    // Getter & Setter untuk kota
    public static String getKota() {
        return kota;
    }

    public static void setKota(String kota) {
        Session.kota = kota;
    }

    // Getter & Setter untuk foto profil
    public static String getFotoProfilPath() {
        return fotoProfilPath;
    }

    public static void setFotoProfilPath(String path) {
        Session.fotoProfilPath = path;
    }

    // Method optional: Reset seluruh session (misalnya saat logout)
    public static void resetSession() {
        currentUserId = null;
        namaDepan = null;
        namaBelakang = null;
        telepon = null;
        alamat = null;
        kota = null;
        fotoProfilPath = null;
    }
}
