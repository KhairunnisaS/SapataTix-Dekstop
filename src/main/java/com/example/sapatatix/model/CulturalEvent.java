package com.example.sapatatix.model;

public class CulturalEvent extends Event {
    private String artisTampil;
    private String genreBudaya;

    public CulturalEvent(String id, String judul, String kategori, String tempat, String deskripsi,
                         String namaHost, String noHpHost, String sesi, String tanggalMulai,
                         String waktuMulai, String waktuBerakhir, String jenisEvent, String bannerUrl, String userId,
                         String artisTampil, String genreBudaya) {
        super(id, judul, kategori, tempat, deskripsi, namaHost, noHpHost, sesi,
                tanggalMulai, waktuMulai, waktuBerakhir, jenisEvent, bannerUrl, userId);
        this.artisTampil = artisTampil;
        this.genreBudaya = genreBudaya;
    }

    @Override
    public String getJenisSpesifik() {
        return "Festival Budaya";
    }

    @Override
    public String getDetailTambahan() {
        return "Artis Tampil: " + artisTampil + ", Genre: " + genreBudaya;
    }

    // --- Getters untuk field spesifik ---
    public String getArtisTampil() { return artisTampil; }
    public String getGenreBudaya() { return genreBudaya; }
}