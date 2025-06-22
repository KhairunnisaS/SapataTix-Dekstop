package com.example.sapatatix.model;

import org.json.JSONObject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Kelas abstrak karena tidak semua event adalah event dasar, melainkan jenis spesifik
public abstract class Event {
    protected String id; // ID dari database
    protected String judul;
    protected String kategori; // Contoh: "Budaya", "Pariwisata", "Amal"
    protected String tempat;
    protected String deskripsi;
    protected String namaHost;
    protected String noHpHost;
    protected String sesi; // Pagi, Siang, Sore, Malam
    protected LocalDate tanggalMulai;
    protected LocalTime waktuMulai;
    protected LocalTime waktuBerakhir;
    protected String jenisEvent; // "Event Satu Hari", "Event Berjalan"
    protected String bannerUrl;
    protected String userId; // ID pengguna yang membuat event

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
        this.tanggalMulai = LocalDate.parse(tanggalMulai); // Pastikan format tanggal sesuai ISO_LOCAL_DATE (YYYY-MM-DD)
        this.waktuMulai = LocalTime.parse(waktuMulai);    // Pastikan format waktu sesuai ISO_LOCAL_TIME (HH:MM)
        this.waktuBerakhir = LocalTime.parse(waktuBerakhir); // Pastikan format waktu sesuai ISO_LOCAL_TIME (HH:MM)
        this.jenisEvent = jenisEvent;
        this.bannerUrl = bannerUrl;
        this.userId = userId;
    }

    // Metode abstrak yang harus diimplementasikan oleh setiap subclass event
    public abstract String getJenisSpesifik(); // Misal: "Festival Tari", "Tur Kota"
    public abstract String getDetailTambahan(); // Misal: "Artis Tampil: X, Y", "Durasi: 3 jam"

    // Metode umum untuk semua event
    public String getFormattedTanggal() {
        return tanggalMulai.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
    }

    public String getFormattedWaktu() {
        return waktuMulai.format(DateTimeFormatter.ofPattern("HH:mm")) + " - " + waktuBerakhir.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // --- Getters untuk semua field umum ---
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

    // Metode utilitas untuk membuat objek Event dari JSONObject (Ini akan perlu diadaptasi)
    public static Event fromJson(JSONObject json) {
        // Ini adalah tempat di mana polimorfisme akan diterapkan saat membuat objek event.
        // Berdasarkan 'kategori' atau field lain, kita akan menginstansiasi subclass yang tepat.
        String kategori = json.optString("kategori", "");
        switch (kategori) {
            case "Budaya":
                return new CulturalEvent(
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        json.optString("artis_tampil", "N/A"), json.optString("genre_budaya", "N/A") // Atribut spesifik
                );
            case "Pariwisata":
                return new TourismEvent(
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        json.optString("durasi_tur", "N/A"), json.optString("fasilitas_termasuk", "N/A") // Atribut spesifik
                );
            // Tambahkan case untuk CharityEvent dan kategori lainnya
            default:
                // Jika tidak ada subclass spesifik, kembalikan event dasar atau event default
                // Atau lempar exception jika semua event harus bertipe spesifik
                return new CulturalEvent( // Contoh fallback, Anda bisa buat kelas DefaultEvent jika perlu
                        json.optString("id"), json.optString("judul"), json.optString("kategori"),
                        json.optString("tempat"), json.optString("deskripsi"), json.optString("nama_host"),
                        json.optString("no_hp_host"), json.optString("sesi"), json.optString("tanggal_mulai"),
                        json.optString("waktu_mulai"), json.optString("waktu_berakhir"), json.optString("jenis_event"),
                        json.optString("banner_url"), json.optString("user_id"),
                        "N/A", "N/A" // Default values for specific attributes
                );
        }
    }
}