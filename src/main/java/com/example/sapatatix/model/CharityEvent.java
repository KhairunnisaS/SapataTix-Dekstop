package com.example.sapatatix.model;

public class CharityEvent extends Event {
    private String organisasiAmal;
    private String tujuanDonasi;

    public CharityEvent(String id, String judul, String kategori, String tempat, String deskripsi,
                        String namaHost, String noHpHost, String sesi, String tanggalMulai,
                        String waktuMulai, String waktuBerakhir, String jenisEvent, String bannerUrl, String userId,
                        String organisasiAmal, String tujuanDonasi) {
        super(id, judul, kategori, tempat, deskripsi, namaHost, noHpHost, sesi,
                tanggalMulai, waktuMulai, waktuBerakhir, jenisEvent, bannerUrl, userId);
        this.organisasiAmal = organisasiAmal;
        this.tujuanDonasi = tujuanDonasi;
    }

    @Override
    public String getJenisSpesifik() {
        return "Konser Amal";
    }

    @Override
    public String getDetailTambahan() {
        return "Organisasi: " + organisasiAmal + ", Tujuan Donasi: " + tujuanDonasi;
    }

    // --- Getters untuk field spesifik ---
    public String getOrganisasiAmal() { return organisasiAmal; }
    public String getTujuanDonasi() { return tujuanDonasi; }
}