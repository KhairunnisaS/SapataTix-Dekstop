package com.example.sapatatix.model;

public class TourismEvent extends Event {
    private String durasiTur;
    private String fasilitasTermasuk;

    public TourismEvent(String id, String judul, String kategori, String tempat, String deskripsi,
                        String namaHost, String noHpHost, String sesi, String tanggalMulai,
                        String waktuMulai, String waktuBerakhir, String jenisEvent, String bannerUrl, String userId,
                        String durasiTur, String fasilitasTermasuk) {
        super(id, judul, kategori, tempat, deskripsi, namaHost, noHpHost, sesi,
                tanggalMulai, waktuMulai, waktuBerakhir, jenisEvent, bannerUrl, userId);
        this.durasiTur = durasiTur;
        this.fasilitasTermasuk = fasilitasTermasuk;
    }

    @Override
    public String getJenisSpesifik() {
        return "Tur Pariwisata";
    }

    @Override
    public String getDetailTambahan() {
        return "Durasi Tur: " + durasiTur + ", Fasilitas: " + fasilitasTermasuk;
    }

    // --- Getters untuk field spesifik ---
    public String getDurasiTur() { return durasiTur; }
    public String getFasilitasTermasuk() { return fasilitasTermasuk; }
}