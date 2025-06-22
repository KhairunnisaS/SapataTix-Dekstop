package com.example.sapatatix.model;

import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Event {
    // ENKAPSULASI: Atribut dideklarasikan sebagai protected, membatasi akses langsung
    protected String id;
    protected String judul;
    protected String kategori;
    protected String tempat;
    protected String deskripsi;
    protected String namaHost;
    protected String noHpHost;
    protected String sesi;
    protected LocalDate tanggalMulai;
    protected LocalTime waktuMulai;
    protected LocalTime waktuBerakhir;
    protected String jenisEvent;
    protected String bannerUrl;
    protected String userId;

    // Konstruktor dasar untuk semua jenis event
    public Event(String id, String judul, String kategori, String tempat, String deskripsi,
                 String namaHost, String noHpHost, String sesi, String tanggalMulai,
                 String waktuMulai, String waktuBerakhir, String jenisEvent, String bannerUrl, String userId) {
        this.id = id;
        this.judul = judul;
        this.kategori = kategori;
        this.tempat = tempat;
        this.deskripsi = deskripsi;
        this.namaHost = namaHost;
        this.noHpHost = noHpHost;
        this.sesi = sesi;
        this.tanggalMulai = LocalDate.parse(tanggalMulai);
        this.waktuMulai = LocalTime.parse(waktuMulai);
        this.waktuBerakhir = LocalTime.parse(waktuBerakhir);
        this.jenisEvent = jenisEvent;
        this.bannerUrl = bannerUrl;
        this.userId = userId;
    }

    // POLIMORFISME: Metode abstrak yang harus diimplementasikan oleh setiap subclass Event.
    // Perilaku spesifik akan ditentukan oleh subclass (misal: CulturalEvent, TourismEvent, CharityEvent).
    public abstract String getJenisSpesifik(); // Misal: "Festival Tari", "Tur Kota"
    public abstract String getDetailTambahan(); // Misal: "Artis Tampil: X, Y", "Durasi: 3 jam"

    // ENKAPSULASI: Metode publik yang menyediakan representasi data yang diformat,
    // menyembunyikan detail implementasi format tanggal/waktu.
    public String getFormattedTanggal() {
        return tanggalMulai.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM[yyyy]"));
    }

    // ENKAPSULASI: Metode publik yang menyediakan representasi data yang diformat.
    public String getFormattedWaktu() {
        return waktuMulai.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + waktuBerakhir.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ENKAPSULASI: Metode publik (getters) untuk mengakses nilai atribut protected.
    // Ini adalah bagian dari antarmuka publik kelas, menyediakan kontrol akses ke data internal.
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getKategori() { return kategori; }
    public String getTempat() { return tempat; }
    public String getDeskripsi() { return deskripsi; }
    public String getNamaHost() { return namaHost; }
    public String getNoHpHost() { return noHpHost; }
    public String getSesi() { return sesi; }
    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public LocalTime getWaktuMulai() { return waktuMulai; }
    public LocalTime getWaktuBerakhir() { return waktuBerakhir; }
    public String getJenisEvent() { return jenisEvent; }
    public String getBannerUrl() { return bannerUrl; }
    public String getUserId() { return userId; }

    // POLIMORFISME: Metode pabrik statis yang membuat objek subclass Event yang spesifik
    // berdasarkan nilai 'kategori' dalam JSONObject. Ini adalah titik di mana polimorfisme
    // runtime terjadi karena tipe objek yang dikembalikan bisa berbeda (CulturalEvent, TourismEvent, dll.)
    // meskipun dikembalikan sebagai tipe Event.
    public static Event fromJson(JSONObject json) {
        String kategori = json.optString("kategori", "");
        switch (kategori) {
            case "Budaya":
                return new CulturalEvent( // POLIMORFISME: Mengembalikan instance CulturalEvent
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        json.optString("artis_tampil", "N/A"), json.optString("genre_budaya", "N/A")
                );
            case "Pariwisata":
                return new TourismEvent( // POLIMORFISME: Mengembalikan instance TourismEvent
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        json.optString("durasi_tur", "N/A"), json.optString("fasilitas_termasuk", "N/A")
                );
            // Tambahkan case untuk CharityEvent dan kategori lainnya
            default:
                return new CulturalEvent( // POLIMORFISME: Fallback, mengembalikan instance CulturalEvent
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        "N/A", "N/A"
                );
        }
    }
}